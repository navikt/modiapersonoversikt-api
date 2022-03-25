package no.nav.modiapersonoversikt.consumer.sak

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.matching.AnythingPattern
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import no.nav.common.log.MDCConstants
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.modiapersonoversikt.infrastructure.RestConstants
import no.nav.modiapersonoversikt.utils.WireMockUtils
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.MDC
import java.time.OffsetDateTime
import java.time.ZoneOffset

internal class SakApiTest {
    @MockK
    private lateinit var stsService: SystemUserTokenProvider

    private val AKTOERID = "11111"
    private val opprettetDato = OffsetDateTime.of(
        2019,
        10,
        23,
        15,
        45,
        12,
        529000000,
        ZoneOffset.UTC
    )
    private val sakDto = SakDto(
        id = "141481247",
        tema = "BAR",
        applikasjon = "IT01",
        aktoerId = "1000006766539",
        fagsakNr = "0326A02",
        opprettetAv = "srvRuting",
        opprettetTidspunkt = opprettetDato
    )
    private val sakDtoJson = """
        {
            "id": 141481247,
            "tema": "BAR",
            "applikasjon": "IT01",
            "aktoerId": 1000006766539,
            "orgnr": null,
            "fagsakNr": "0326A02",
            "opprettetAv": "srvRuting",
            "opprettetTidspunkt": "2019-10-23T17:45:12.529+02:00"
        }
    """.trimIndent()
    private val sakDtoListe = "[$sakDtoJson]"

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        every { stsService.systemUserToken } returns ("TOKEN")
        MDC.put(MDCConstants.MDC_CALL_ID, "MDC_CALL_ID")
    }

    @Test
    fun `hent saker som list av data objects fra SakApi`() {
        withMockGateway(stub = WireMockUtils.getWithBody(statusCode = 200, body = sakDtoListe)) { sakApi ->
            val response = sakApi.hentSaker(AKTOERID)
            MatcherAssert.assertThat(response.size, Is.`is`(1))
            MatcherAssert.assertThat(response[0].opprettetTidspunkt, Is.`is`(opprettetDato))
        }
    }

    @Test
    fun `handterer status coder utenfor 200-299 rangen`() {
        withMockGateway(stub = WireMockUtils.getWithBody(statusCode = 404, body = sakDtoListe)) { sakApi ->
            assertThrows<IllegalStateException> {
                sakApi.hentSaker(AKTOERID)
            }
        }

        withMockGateway(stub = WireMockUtils.getWithBody(statusCode = 500)) { sakApi ->
            assertThrows<IllegalStateException> {
                sakApi.hentSaker(AKTOERID)
            }
        }
    }

    @Test
    fun `skal kunne opprette Sak og parse resultatet`() {
        withMockGateway(
            verify = { server -> verifyHeaders(server, WireMock.postRequestedFor(WireMock.urlEqualTo("/api/v1/saker")))() },
            stub = WireMockUtils.postWithBody(statusCode = 200, body = sakDtoJson)
        ) { sakApi ->
            val opprettetDto = sakApi.opprettSak(
                OpprettSakDto(
                    aktoerId = sakDto.aktoerId!!,
                    tema = sakDto.tema!!,
                    fagsakNr = sakDto.fagsakNr!!,
                    applikasjon = sakDto.applikasjon!!,
                    opprettetAv = sakDto.opprettetAv!!
                )
            )

            MatcherAssert.assertThat(opprettetDto, Is.`is`(sakDto))
        }
    }

    private fun verifyHeaders(server: WireMockServer, call: RequestPatternBuilder): () -> Unit = {
        server.verify(
            call
                .withHeader("X-Correlation-ID", AnythingPattern())
                .withHeader(RestConstants.AUTHORIZATION, AnythingPattern())
                .withHeader("accept", WireMock.matching("application/json"))
        )
    }

    private fun withMockGateway(
        stub: WireMockServer.() -> Unit = { },
        verify: ((WireMockServer) -> Unit)? = null,
        test: (SakApi) -> Unit
    ) {
        WireMockUtils.withMockGateway(
            stub = stub,
            verify = {
                if (verify == null) {
                    verifyHeaders(
                        it,
                        WireMock.getRequestedFor(WireMock.urlEqualTo("/api/v1/saker?aktoerId=$AKTOERID"))
                    )()
                }
            },
            test = { url ->
                val client = SakApiImpl(
                    baseUrl = url,
                    stsService = stsService
                )
                test(client)
            }
        )
    }
}

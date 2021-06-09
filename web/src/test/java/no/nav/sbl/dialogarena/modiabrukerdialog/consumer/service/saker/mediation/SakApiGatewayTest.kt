package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.matching.AnythingPattern
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import no.nav.common.log.MDCConstants
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestConstants
import org.bouncycastle.crypto.tls.ConnectionEnd.server
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.MDC
import java.time.OffsetDateTime
import java.time.ZoneOffset

internal class SakApiGatewayTest {
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
        withMockGateway(stub = getWithBody(statusCode = 200, body = sakDtoListe)) { sakApiGateway ->
            val response = sakApiGateway.hentSaker(AKTOERID)
            assertThat(response.size, `is`(1))
            assertThat(response[0].opprettetTidspunkt, `is`(opprettetDato))
        }
    }

    @Test
    fun `handterer status coder utenfor 200-299 rangen`() {
        withMockGateway(stub = getWithBody(statusCode = 404, body = sakDtoListe)) { sakApiGateway ->
            assertThrows<IllegalStateException> {
                sakApiGateway.hentSaker(AKTOERID)
            }
        }

        withMockGateway(stub = getWithBody(statusCode = 500)) { sakApiGateway ->
            assertThrows<IllegalStateException> {
                sakApiGateway.hentSaker(AKTOERID)
            }
        }
    }

    @Test
    fun `skal kunne opprette Sak og parse resultatet`() {
        withMockGateway(
            verify = { server -> verifyHeaders(server, postRequestedFor(urlEqualTo("/api/v1/saker")))() },
            stub = postWithBody(statusCode = 200, body = sakDtoJson)
        ) { sakApiGateway ->
            val opprettetDto = sakApiGateway.opprettSak(
                OpprettSakDto(
                    aktoerId = sakDto.aktoerId!!,
                    tema = sakDto.tema!!,
                    fagsakNr = sakDto.fagsakNr!!,
                    applikasjon = sakDto.applikasjon!!,
                    opprettetAv = sakDto.opprettetAv!!
                )
            )

            assertThat(opprettetDto, `is`(sakDto))
        }
    }

    private fun getWithBody(statusCode: Int = 200, body: String? = null): WireMockServer.() -> Unit = {
        this.stubFor(get(anyUrl()).withBody(statusCode, body))
    }

    private fun postWithBody(statusCode: Int = 200, body: String? = null): WireMockServer.() -> Unit = {
        this.stubFor(post(anyUrl()).withBody(statusCode, body))
    }

    private fun MappingBuilder.withBody(statusCode: Int = 200, body: String? = null): MappingBuilder {
        this.willReturn(
            aResponse()
                .withStatus(statusCode)
                .withHeader("Content-Type", "application/json")
                .withBody(body)
        )
        return this
    }

    private fun verifyHeaders(server: WireMockServer, call: RequestPatternBuilder): () -> Unit = {
        server.verify(
            call
                .withHeader("X-Correlation-ID", AnythingPattern())
                .withHeader(RestConstants.AUTHORIZATION, AnythingPattern())
                .withHeader("accept", matching("application/json"))
        )
    }

    private fun withMockGateway(
        stub: WireMockServer.() -> Unit = { },
        verify: ((WireMockServer) -> Unit)? = null,
        test: (SakApiGateway) -> Unit
    ) {
        val wireMockServer = WireMockServer()
        try {
            stub(wireMockServer)
            wireMockServer.start()

            val client = SakApiGatewayImpl("http://localhost:${wireMockServer.port()}", stsService)
            test(client)

            if (verify == null) {
                verifyHeaders(wireMockServer, getRequestedFor(urlEqualTo("/api/v1/saker?aktoerId=$AKTOERID")))()
            } else {
                verify(wireMockServer)
            }
        } finally {
            wireMockServer.stop()
        }
    }
}

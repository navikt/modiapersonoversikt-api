package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.matching.AnythingPattern
import no.nav.common.log.MDCConstants
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestConstants
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.FodselnummerAktorService
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.slf4j.MDC

internal class SakApiGatewayTest {

    @Mock
    private lateinit var fodselnummerAktorService: FodselnummerAktorService

    @Mock
    private lateinit var stsService: SystemUserTokenProvider

    private val AKTOERID = "11111"
    private val FNR = "000000001"
    private val response = """
                            [{
                                "id": 141481247,
                                "tema": "BAR",
                                "applikasjon": "IT01",
                                "aktoerId": 1000006766539,
                                "orgnr": null,
                                "fagsakNr": "0326A02",
                                "opprettetAv": "srvRuting",
                                "opprettetTidspunkt": "2019-10-23T17:45:12.529+02:00"
                              }]""".trimIndent()

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(fodselnummerAktorService.hentAktorIdForFnr(FNR)).thenReturn(AKTOERID)
        Mockito.`when`(stsService.systemUserToken).thenReturn("TOKEN")
        MDC.put(MDCConstants.MDC_CALL_ID, "MDC_CALL_ID")

    }

    @Test
    fun `hent saker som list av data objects fra SakApi`() {
        withMockGateway(statusCode = 200, body = response) { sakApiGateway ->
            val response = sakApiGateway.hentSaker(FNR)
            MatcherAssert.assertThat(response.size, `is`(1))

        }
    }

    @Test
    fun `handterer status coder utenfor 200-299 rangen`() {
        withMockGateway(statusCode = 404, body = response) { sakApiGateway ->
            MatcherAssert.assertThat(sakApiGateway.hentSaker(FNR).isEmpty(), `is`(true))
        }

        withMockGateway(statusCode = 500) { sakApiGateway ->
            MatcherAssert.assertThat(sakApiGateway.hentSaker(FNR).isEmpty(), `is`(true))
        }
    }

    internal fun withMockGateway(statusCode: Int = 200, body: String? = null, test: (SakApiGateway) -> Unit) {
        val wireMockServer = WireMockServer()

        wireMockServer.stubFor(get(anyUrl())
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)))
        wireMockServer.start()

        val client = SakApiGatewayImpl(fodselnummerAktorService, "http://localhost:${wireMockServer.port()}", stsService)
        test(client)


        verify(
                getRequestedFor(urlEqualTo("/api/v1/saker?aktoerId=$AKTOERID"))
                        .withHeader(RestConstants.NAV_CALL_ID_HEADER, AnythingPattern())
                        .withHeader("accept", matching("application/json"))
        )
        wireMockServer.stop()
    }

}

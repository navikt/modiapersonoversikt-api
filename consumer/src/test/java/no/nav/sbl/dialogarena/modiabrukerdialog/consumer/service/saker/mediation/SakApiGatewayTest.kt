package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.matching.AnythingPattern
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import no.nav.common.log.MDCConstants
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentIdent
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestConstants
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.MDC

internal class SakApiGatewayTest {

    @MockK
    private lateinit var pdlOpslagService: PdlOppslagService

    @MockK
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

    private val withHeader = {
        verify(getRequestedFor(urlEqualTo("/api/v1/saker?aktoerId=$AKTOERID"))
                .withHeader(RestConstants.NAV_CALL_ID_HEADER, AnythingPattern())
                .withHeader("accept", matching("application/json")))
    }

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        val identer = listOf(
                HentIdent.IdentInformasjon(AKTOERID, HentIdent.IdentGruppe.AKTORID)
        )

        every { pdlOpslagService.hentIdent(FNR) } returns (HentIdent.Identliste(identer))
        every { stsService.systemUserToken } returns ("TOKEN")
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
    fun `handterer null ident informasjon fra pdl oppslag`() {
        every { pdlOpslagService.hentIdent(FNR) } returns (null)
        withMockGateway(verify = {}) { sakApiGateway ->
            MatcherAssert.assertThat(sakApiGateway.hentSaker(FNR).isEmpty(), `is`(true))
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

    internal fun withMockGateway(statusCode: Int = 200,
                                 body: String? = null,
                                 verify: (() -> Unit)? = withHeader,
                                 test: (SakApiGateway) -> Unit) {
        val wireMockServer = WireMockServer()

        wireMockServer.stubFor(get(anyUrl())
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)))
        wireMockServer.start()

        val client = SakApiGatewayImpl(pdlOpslagService, "http://localhost:${wireMockServer.port()}", stsService)
        test(client)

        if (verify != null) verify()

        wireMockServer.stop()
    }

}

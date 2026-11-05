package no.nav.modiapersonoversikt.service.sfhenvendelse

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.PlainJWT
import io.mockk.every
import io.mockk.mockk
import no.nav.common.auth.context.AuthContext
import no.nav.common.auth.context.UserRole
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.utils.BoundedOnBehalfOfTokenClient
import no.nav.modiapersonoversikt.utils.Utils
import no.nav.modiapersonoversikt.utils.WireMockUtils.get
import no.nav.modiapersonoversikt.utils.WireMockUtils.json
import no.nav.modiapersonoversikt.utils.WireMockUtils.status
import no.nav.modiapersonoversikt.utils.readResource
import okhttp3.OkHttpClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class SfHenvendelseServiceIntegrationTest {
    companion object {
        @JvmField
        @RegisterExtension
        val wiremock = WireMockExtension.newInstance().build()

        private val httpClient = OkHttpClient()
        private val testSubject =
            AuthContext(
                UserRole.INTERN,
                PlainJWT(JWTClaimsSet.Builder().subject("Z999999").build()),
            )
    }

    private val meldinger: String = SfHenvendelseServiceIntegrationTest.readResource("mock-sf-meldinger.json")

    @Test
    fun `kan hente meldinger`() {
        wiremock.get(WireMock.urlPathEqualTo("/henvendelseinfo/henvendelseliste/v2")) {
            status(200)
            json(meldinger)
        }
        Utils.withProperty("SF_HENVENDELSE_URL", "http://localhost:${wiremock.port}") {
            val oboTokenProvider = mockk<BoundedOnBehalfOfTokenClient>()
            every { oboTokenProvider.exchangeOnBehalfOfToken(testSubject.idToken.serialize()) } returns "OBO-TOKEN"

            AuthContextUtils.withContext(testSubject) {
                val api = SfHenvendelseApiFactory.createHenvendelseInfoApi(httpClient)
                val result = api.henvendelseinfoHenvendelselisteV2Get("aktorid", "coorId")
                assertThat(result?.data).hasSize(1)
            }
        }
    }

    @Test
    fun `grupperingsfeil i v2 henter henvendelser fra v1 som fallback`() {
        wiremock.get(WireMock.urlPathEqualTo("/henvendelseinfo/henvendelseliste/v2")) {
            status(200)
            json(listPayload("SAMTALEREFERAT", "[]"))
        }
        wiremock.get(WireMock.urlPathEqualTo("/henvendelseinfo/henvendelseliste")) {
            status(200)
            json(
                listPayload(
                    "SAMTALEREFERAT",
                    """
                    [
                      {"meldingsId":"v1-message-1","sendtDato":"2021-02-02T12:37:37.000+0000","fritekst":"første v1 melding","fra":{"identType":"NAVIDENT","ident":"Z000001"}},
                      {"meldingsId":"v1-message-2","sendtDato":"2021-02-02T12:38:37.000+0000","fritekst":"andre v1 melding","fra":{"identType":"NAVIDENT","ident":"Z000001"}}
                    ]
                    """.trimIndent(),
                    "v1-thread",
                ),
            )
        }

        Utils.withProperty("SF_HENVENDELSE_URL", "http://localhost:${wiremock.port}") {
            val oboTokenProvider = mockk<BoundedOnBehalfOfTokenClient>()
            every { oboTokenProvider.exchangeOnBehalfOfToken(testSubject.idToken.serialize()) } returns "OBO-TOKEN"
            val ansattService = mockk<AnsattService>()
            every { ansattService.hentAnsattFagomrader(any()) } returns setOf("DAG")
            val api = SfHenvendelseApiFactory.createHenvendelseInfoApi(httpClient)
            val service =
                SfHenvendelseServiceImpl(
                    mockk<PdlOppslagService>(),
                    mockk<NorgApi>(),
                    ansattService,
                    httpClient,
                    henvendelseInfoApi = api,
                )

            AuthContextUtils.withContext(testSubject) {
                val result = service.hentHenvendelser(EksternBruker.AktorId("00000000000000"), "0101")

                assertThat(result).hasSize(1)
                assertThat(result.single().kjedeId).isEqualTo("v1-thread")
                assertThat(result.single().meldinger).hasSize(2)
                assertThat(result.single().meldinger!!.map { it.meldingsId }).containsExactly(
                    "v1-message-1",
                    "v1-message-2",
                )
            }
        }
    }

    private fun listPayload(
        type: String,
        messages: String,
        chainId: String = "invalid-v2-thread",
    ): String =
        """
        {
          "data": [{
            "tilknyttetEnhet": null,
            "opprinneligGT": "0101",
            "opprinneligEnhet": null,
            "opprettetDato": "2021-02-02T12:37:37.000+0000",
            "meldinger": $messages,
            "markeringer": null,
            "kjedeId": "$chainId",
            "kasseringsDato": null,
            "journalposter": [],
            "henvendelseType": "$type",
            "henvendelseId": null,
            "gjeldendeTemagruppe": "DAG",
            "gjeldendeTema": null,
            "fnr": "12345678910",
            "feilsendt": false,
            "avsluttetDato": null,
            "aktorId": "00000000000000"
          }],
          "currentPage": 1,
          "totalPages": 1,
          "pageSize": 100,
          "hasNextPage": false
        }
        """.trimIndent()
}

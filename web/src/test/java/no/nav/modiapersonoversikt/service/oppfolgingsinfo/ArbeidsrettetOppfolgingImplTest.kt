package no.nav.modiapersonoversikt.service.oppfolgingsinfo

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.PlainJWT
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.common.auth.context.AuthContext
import no.nav.common.auth.context.UserRole
import no.nav.common.types.identer.Fnr
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.commondomain.Veileder
import no.nav.modiapersonoversikt.consumer.veilarboppfolging.ArbeidsrettetOppfolging
import no.nav.modiapersonoversikt.consumer.veilarboppfolging.ArbeidsrettetOppfolgingServiceImpl
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.modiapersonoversikt.utils.BoundedOnBehalfOfTokenClient
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.net.URL

class ArbeidsrettetOppfolgingImplTest {
    companion object {
        @JvmField
        @RegisterExtension
        val wiremock = WireMockExtension.newInstance().build()

        private const val FNR = "12345678910"
        private val testSubject =
            AuthContext(
                UserRole.INTERN,
                PlainJWT(JWTClaimsSet.Builder().subject("Z123456").build()),
            )
    }

    @Test
    fun `henter ut oppfolgingsstatus for bruker under oppfolging`() {
        val (apiClient) = setup(underOppfolging = true)

        val oppfolgingsinfo: ArbeidsrettetOppfolging.Info =
            AuthContextUtils.withContext(testSubject) {
                apiClient.hentOppfolgingsinfo(Fnr(FNR))
            }

        assertThat(oppfolgingsinfo.erUnderOppfolging).isTrue
        assertThat(oppfolgingsinfo.veileder?.ident).isEqualTo("ident")
        assertThat(oppfolgingsinfo.veileder?.fornavn).isEqualTo("fornavn")
        assertThat(oppfolgingsinfo.veileder?.etternavn).isEqualTo("etternavn")
        assertThat(oppfolgingsinfo.oppfolgingsenhet?.enhetId).isEqualTo("1234")
        assertThat(oppfolgingsinfo.oppfolgingsenhet?.navn).isEqualTo("NAV Enhet")
    }

    @Test
    fun `henter ut oppfolgingsstatus for bruker ikke under oppfolging`() {
        val (apiClient, ansattService) = setup(underOppfolging = false)

        val oppfolgingsinfo: ArbeidsrettetOppfolging.Info =
            AuthContextUtils.withContext(testSubject) {
                apiClient.hentOppfolgingsinfo(Fnr(FNR))
            }

        verify { ansattService wasNot Called }
        assertThat(oppfolgingsinfo.erUnderOppfolging).isFalse
        assertThat(oppfolgingsinfo.veileder).isNull()
        assertThat(oppfolgingsinfo.oppfolgingsenhet).isNull()
    }

    private fun setup(underOppfolging: Boolean): Pair<ArbeidsrettetOppfolging.Service, AnsattService> {
        gittOppfolgingStatusResponse(underOppfolging)
        if (underOppfolging) {
            gittOppfolgingsEnhetOgVeilederResponse()
        }

        val ansattService = mockk<AnsattService>()
        every { ansattService.hentVeileder(eq(NavIdent("Z999999"))) } returns
            Veileder(
                "fornavn",
                "etternavn",
                "ident",
            )

        val oboTokenProvider = mockk<BoundedOnBehalfOfTokenClient>()
        every { oboTokenProvider.exchangeOnBehalfOfToken(testSubject.idToken.serialize()) } returns "OBO-TOKEN"

        val gqlClient =
            GraphQLKtorClient(
                url = URL("http://localhost:${wiremock.port}/api/graphql"),
                httpClient = HttpClient(OkHttp),
            )

        val apiClient =
            ArbeidsrettetOppfolgingServiceImpl(
                apiUrl = "http://localhost:${wiremock.port}",
                ansattService = ansattService,
                graphQLClient = gqlClient,
                oboTokenClient = oboTokenProvider,
            )
        return Pair(apiClient, ansattService)
    }

    private fun gittOppfolgingStatusResponse(underOppfolging: Boolean) {
        @Language("json")
        val body =
            """
            {
                "data": {
                    "oppfolging": { "erUnderOppfolging": $underOppfolging },
                    "brukerStatus": { "manuell": { "erManuell": true } }
                }
            }
            """.trimIndent()

        wiremock.stubFor(
            post(urlEqualTo("/api/graphql"))
                .withRequestBody(matchingJsonPath("$.operationName", equalTo("HentOppfolgingStatus")))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body),
                ),
        )
    }

    private fun gittOppfolgingsEnhetOgVeilederResponse() {
        @Language("json")
        val body =
            """
            {
                "data": {
                    "oppfolgingsEnhet": {
                        "enhet": { "id": "1234", "navn": "NAV Enhet" }
                    },
                    "brukerStatus": {
                        "veilederTilordning": { "veilederIdent": "Z999999" }
                    }
                }
            }
            """.trimIndent()

        wiremock.stubFor(
            post(urlEqualTo("/api/graphql"))
                .withRequestBody(matchingJsonPath("$.operationName", equalTo("HentOppfolgingsEnhetOgVeileder")))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body),
                ),
        )
    }
}

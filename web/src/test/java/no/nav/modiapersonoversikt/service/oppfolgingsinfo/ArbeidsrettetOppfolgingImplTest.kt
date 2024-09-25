package no.nav.modiapersonoversikt.service.oppfolgingsinfo

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.marcinziolo.kotlin.wiremock.post
import com.marcinziolo.kotlin.wiremock.returnsJson
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.PlainJWT
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
import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.modiapersonoversikt.utils.BoundedOnBehalfOfTokenClient
import no.nav.modiapersonoversikt.utils.WireMockUtils.get
import no.nav.modiapersonoversikt.utils.WireMockUtils.json
import no.nav.modiapersonoversikt.utils.WireMockUtils.status
import okhttp3.OkHttpClient
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class ArbeidsrettetOppfolgingImplTest {
    companion object {
        @JvmField
        @RegisterExtension
        val wiremock = WireMockExtension.newInstance().build()

        private val httpClient = OkHttpClient()
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
        gittUnderOppfolging(underOppfolging)
        gittOppfolgingStatus()

        val unleashService = mockk<UnleashService>()
        every { unleashService.isEnabled(Feature.LOG_REQUEST_BODY.propertyKey) } returns true
        every { unleashService.isEnabled(Feature.LOG_REQUEST_BODY.propertyKey) } returns true

        val ansattService = mockk<AnsattService>()
        every { ansattService.hentVeileder(eq(NavIdent("Z999999"))) } returns
            Veileder(
                "fornavn",
                "etternavn",
                "ident",
            )

        val oboTokenProvider = mockk<BoundedOnBehalfOfTokenClient>()
        every { oboTokenProvider.exchangeOnBehalfOfToken(testSubject.idToken.serialize()) } returns "OBO-TOKEN"

        val apiClient =
            ArbeidsrettetOppfolgingServiceImpl(
                apiUrl = "http://localhost:${wiremock.port}",
                ansattService = ansattService,
                httpClient = httpClient,
            )
        return Pair(apiClient, ansattService)
    }

    private fun gittOppfolgingStatus() {
        @Language("json")
        val returnbody =
            """
            {
                "oppfolgingsenhet": {
                  "navn": "NAV Enhet",
                  "enhetId": "1234"
                },
                "veilederId": "Z999999",
                "formidlingsgruppe": "IARBS"
            }
            """.trimIndent()

        wiremock
            .post {
                urlMatching("/v2/person/hent-oppfolgingsstatus")
            }.returnsJson {
                status(200)
                body = returnbody
            }
    }

    private fun gittUnderOppfolging(underOppfolging: Boolean) {
        @Language("json")
        val body =
            """
            {
                "erManuell": true,
                "underOppfolging": $underOppfolging
            }
            """.trimIndent()

        wiremock.get(urlMatching("/underoppfolging\\?fnr.*")) {
            status(200)
            json(body)
        }
    }
}

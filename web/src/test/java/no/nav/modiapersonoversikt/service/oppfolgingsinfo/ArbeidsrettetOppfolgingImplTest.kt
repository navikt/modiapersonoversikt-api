package no.nav.modiapersonoversikt.service.oppfolgingsinfo

import com.github.tomakehurst.wiremock.client.WireMock.urlMatching
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
import no.nav.common.utils.fn.UnsafeSupplier
import no.nav.modiapersonoversikt.consumer.ldap.LDAPService
import no.nav.modiapersonoversikt.consumer.ldap.Saksbehandler
import no.nav.modiapersonoversikt.consumer.veilarboppfolging.ArbeidsrettetOppfolging
import no.nav.modiapersonoversikt.consumer.veilarboppfolging.ArbeidsrettetOppfolgingServiceImpl
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.utils.WireMockUtils
import no.nav.modiapersonoversikt.utils.WireMockUtils.getWithBody
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ArbeidsrettetOppfolgingImplTest {
    val fnr = "12345678910"
    private val TEST_SUBJECT = AuthContext(
        UserRole.INTERN,
        PlainJWT(JWTClaimsSet.Builder().subject("Z123456").build())
    )

    @Language("json")
    private val statusUnderOppfolgingResponse = """
        {
            "erManuell": true,
            "underOppfolging": true
        }
    """.trimIndent()

    @Language("json")
    private val statusIkkeUnderOppfolgingResponse = """
        {
            "erManuell": true,
            "underOppfolging": false
        }
    """.trimIndent()

    @Language("json")
    private val dataResponse = """
        {
            "oppfolgingsenhet": {
              "navn": "NAV Enhet",
              "enhetId": "1234"
            },
            "veilederId": "Z999999",
            "formidlingsgruppe": "IARBS"
        }
    """.trimIndent()

    @Test
    fun `henter ut oppfolgingsstatus for bruker under oppfolging`() {
        WireMockUtils.withMockGateway(
            stub = arrayOf(
                getWithBody(
                    statusCode = 200,
                    url = urlMatching("/underoppfolging\\?fnr.*"),
                    body = statusUnderOppfolgingResponse
                ),
                getWithBody(statusCode = 200, url = urlMatching("/person/$fnr/oppfolgingsstatus"), body = dataResponse),
            ),
            verify = {}
        ) { url ->
            val ldapService = mockk<LDAPService>()
            every { ldapService.hentVeileder(eq(NavIdent("Z999999"))) } returns Saksbehandler(
                "fornavn",
                "etternavn",
                "ident"
            )

            val oppfolgingsinfo: ArbeidsrettetOppfolging.Info = AuthContextUtils.withContext(
                TEST_SUBJECT,
                UnsafeSupplier {
                    ArbeidsrettetOppfolgingServiceImpl(
                        apiUrl = url,
                        ldapService = ldapService
                    ).hentOppfolgingsinfo(Fnr(fnr))
                }
            )

            assertThat(oppfolgingsinfo.erUnderOppfolging).isTrue
            assertThat(oppfolgingsinfo.veileder?.ident).isEqualTo("ident")
            assertThat(oppfolgingsinfo.veileder?.fornavn).isEqualTo("fornavn")
            assertThat(oppfolgingsinfo.veileder?.etternavn).isEqualTo("etternavn")
            assertThat(oppfolgingsinfo.oppfolgingsenhet?.enhetId).isEqualTo("1234")
            assertThat(oppfolgingsinfo.oppfolgingsenhet?.navn).isEqualTo("NAV Enhet")
        }
    }

    @Test
    fun `henter ut oppfolgingsstatus for bruker ikke under oppfolging`() {
        WireMockUtils.withMockGateway(
            stub = arrayOf(
                getWithBody(
                    statusCode = 200,
                    url = urlMatching("/underoppfolging\\?fnr.*"),
                    body = statusIkkeUnderOppfolgingResponse
                ),
                getWithBody(statusCode = 200, url = urlMatching("/person/$fnr/oppfolgingsstatus"), body = dataResponse),
            ),
            verify = {}
        ) { url ->
            val ldapService = mockk<LDAPService>()
            every { ldapService.hentVeileder(eq(NavIdent("Z999999"))) } returns Saksbehandler(
                "fornavn",
                "etternavn",
                "ident"
            )

            val oppfolgingsinfo: ArbeidsrettetOppfolging.Info = AuthContextUtils.withContext(
                TEST_SUBJECT,
                UnsafeSupplier {
                    ArbeidsrettetOppfolgingServiceImpl(
                        apiUrl = url,
                        ldapService = ldapService
                    ).hentOppfolgingsinfo(Fnr(fnr))
                }
            )

            verify { ldapService wasNot Called }
            assertThat(oppfolgingsinfo.erUnderOppfolging).isFalse
            assertThat(oppfolgingsinfo.veileder).isNull()
            assertThat(oppfolgingsinfo.oppfolgingsenhet).isNull()
        }
    }
}

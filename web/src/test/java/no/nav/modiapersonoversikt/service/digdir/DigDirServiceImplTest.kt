package no.nav.modiapersonoversikt.service.digdir

import DigDirServiceImpl
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.PlainJWT
import io.mockk.every
import io.mockk.mockk
import no.nav.common.auth.context.AuthContext
import no.nav.common.auth.context.UserRole
import no.nav.common.token_client.client.MachineToMachineTokenClient
import no.nav.modiapersonoversikt.utils.WireMockUtils.get
import no.nav.modiapersonoversikt.utils.WireMockUtils.json
import no.nav.modiapersonoversikt.utils.WireMockUtils.status
import no.nav.personoversikt.common.test.testenvironment.TestEnvironmentExtension
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class DigDirServiceImplTest {
    private val machineToMachineTokenClient: MachineToMachineTokenClient = mockk()

    @JvmField
    @RegisterExtension
    val testenvironment = TestEnvironmentExtension(
        mapOf(
            "DIG_DIR_REST_URL" to "http://dummy.no",
            "DIG_DIR_SCOPE" to "dev-gcp:team-rocket:digdir-krr-proxy",
        )
    )

    companion object {
        @JvmField
        @RegisterExtension
        val wiremock: WireMockExtension = WireMockExtension.newInstance().build()
        val testSubject = AuthContext(
            UserRole.INTERN,
            PlainJWT(JWTClaimsSet.Builder().subject("Z999999").build())
        )
    }

    @Language("json")
    private val jsonResponse = """
                {
                  "personident": "10108000398",
                  "aktiv": true,
                  "kanVarsles": false,
                  "reservert": false,
                  "spraak": "nb",
                  "spraakOppdatert": "2000-01-01T09:00:00Z",
                  "epostadresse": "noreply@nav.no",
                  "epostadresseOppdatert": "2000-01-01T09:00:00Z",
                  "epostadresseVerifisert": "2000-01-01T09:00:00Z",
                  "mobiltelefonnummer": "11111111",
                  "mobiltelefonnummerOppdatert": "2000-01-01T09:00:00Z",
                  "mobiltelefonnummerVerifisert": "2000-01-01T09:00:00Z"
                }
    """.trimIndent()

    @Test
    fun `hent kontaktinformasjon fra DigDirRestApi`() {
        every { machineToMachineTokenClient.createMachineToMachineToken(any()) } returns "DIG-DIR-TOKEN"
        wiremock.get {
            status(200)
            json(jsonResponse)
        }
        val digDirRestService = DigDirServiceImpl("http://localhost:${wiremock.port}", machineToMachineTokenClient)
        val response = digDirRestService.hentDigitalKontaktinformasjon("10108000398")
        assertThat(response.personident).isEqualTo("10108000398")
        assertThat(response.epostadresse?.value).isEqualTo("noreply@nav.no")
        assertThat(response.mobiltelefonnummer?.value).isEqualTo("11111111")
    }

    @Test
    fun `trigg feil ved henting av kontaktinformasjon fra DigDirRestApi`() {
        every { machineToMachineTokenClient.createMachineToMachineToken(any()) } returns "DIG-DIR-TOKEN"
        wiremock.get { status(404) }
        val digDirRestService = DigDirServiceImpl("http://localhost:${wiremock.port}", machineToMachineTokenClient)

        val response = digDirRestService.hentDigitalKontaktinformasjon("10108000123")
        assertThat(response.personident).isNull()
        assertThat(response.reservasjon).isEqualTo("")
        assertThat(response.mobiltelefonnummer?.value).isEqualTo("")
        assertThat(response.epostadresse?.value).isEqualTo("")
    }

    @Test
    fun `trigg server-feil ved henting av kontaktinformasjon fra DigDirRestApi`() {
        every { machineToMachineTokenClient.createMachineToMachineToken(any()) } returns "DIG-DIR-TOKEN"
        wiremock.get { status(500) }
        val digDirRestService = DigDirServiceImpl("http://localhost:${wiremock.port}", machineToMachineTokenClient)
        val response = digDirRestService.hentDigitalKontaktinformasjon("10108000123")
        assertThat(response.personident).isNull()
        assertThat(response.reservasjon).isEqualTo("")
        assertThat(response.mobiltelefonnummer?.value).isEqualTo("")
        assertThat(response.epostadresse?.value).isEqualTo("")
    }
}

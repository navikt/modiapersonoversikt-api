package no.nav.modiapersonoversikt.service.dkif

import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.PlainJWT
import no.nav.common.auth.context.AuthContext
import no.nav.common.auth.context.UserRole
import no.nav.modiapersonoversikt.config.AppConstants
import no.nav.modiapersonoversikt.consumer.dkif.DkifServiceRestImpl
import no.nav.modiapersonoversikt.consumer.dkif.generated.infrastructure.ServerException
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.utils.WireMockUtils.get
import no.nav.modiapersonoversikt.utils.WireMockUtils.json
import no.nav.modiapersonoversikt.utils.WireMockUtils.status
import no.nav.personoversikt.test.testenvironment.TestEnvironmentExtension
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.RegisterExtension

internal class DkifServiceRestImplTest {
    companion object {
        @JvmField
        @RegisterExtension
        val testEnvironmentExtension = TestEnvironmentExtension(
            mapOf(
                AppConstants.SYSTEMUSER_USERNAME_PROPERTY to "username"
            )
        )

        @JvmField
        @RegisterExtension
        val wiremock = WireMockExtension.newInstance().build()

        private val testSubject = AuthContext(
            UserRole.INTERN,
            PlainJWT(JWTClaimsSet.Builder().subject("Z999999").build())
        )
    }

    @Language("json")
    private val dkifJsonResponse = """
        {
          "kontaktinfo": {
            "10108000398": {
              "personident": "10108000398",
              "kanVarsles": false,
              "reservert": false,
              "epostadresse": "julenissen@nordpolen.no",
              "mobiltelefonnummer": "12345678"
            },
            "06073000250": {
              "personident": "06073000250",
              "kanVarsles": false,
              "reservert": false,
              "epostadresse": "noreply@nav.no",
              "mobiltelefonnummer": "11111111",
              "spraak": "nb"
            }
          },
          "feil": {
            "10108000123": {
              "melding": "Feil oppstod"
            }
          }
        }
    """.trimIndent()

    @Test
    fun `hent kontaktinformasjon fra DkifRestApi`() {
        wiremock.get {
            status(200)
            json(dkifJsonResponse)
        }
        val dkifRestService = DkifServiceRestImpl("http://localhost:${wiremock.port}")

        val response = AuthContextUtils.withContext(testSubject) {
            dkifRestService.hentDigitalKontaktinformasjon("06073000250")
        }

        assertThat(response.personident).isEqualTo("06073000250")
        assertThat(response.epostadresse?.value).isEqualTo("noreply@nav.no")
        assertThat(response.mobiltelefonnummer?.value).isEqualTo("11111111")
    }

    @Test
    fun `trigg feil ved henting av kontaktinformasjon fra DkifRestApi`() {
        wiremock.get {
            status(200)
            json(dkifJsonResponse)
        }

        val dkifRestService = DkifServiceRestImpl("http://localhost:${wiremock.port}")
        val response = AuthContextUtils.withContext(testSubject) {
            dkifRestService.hentDigitalKontaktinformasjon("10108000123")
        }
        assertThat(response.personident).isNull()
        assertThat(response.reservasjon).isEqualTo("")
        assertThat(response.mobiltelefonnummer?.value).isEqualTo("")
        assertThat(response.epostadresse?.value).isEqualTo("")
    }

    @Test
    fun `trigg server-feil ved henting av kontaktinformasjon fra DkifRestApi`() {
        wiremock.get {
            status(500)
        }
        val dkifRestService = DkifServiceRestImpl("http://localhost:${wiremock.port}")

        assertThrows<ServerException> {
            AuthContextUtils.withContext(testSubject) {
                dkifRestService.hentDigitalKontaktinformasjon("10108000123")
            }
        }
    }
}

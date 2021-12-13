package no.nav.modiapersonoversikt.service.dkif

import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.PlainJWT
import no.nav.common.auth.context.AuthContext
import no.nav.common.auth.context.AuthContextHolderThreadLocal
import no.nav.common.auth.context.UserRole
import no.nav.modiapersonoversikt.legacy.api.domain.dkif.generated.infrastructure.ServerException
import no.nav.modiapersonoversikt.utils.WireMockUtils
import no.nav.modiapersonoversikt.utils.WireMockUtils.getWithBody
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.hamcrest.core.IsNull
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DkifServiceRestImplTest {

    private val TEST_SUBJECT = AuthContext(
        UserRole.INTERN,
        PlainJWT(JWTClaimsSet.Builder().subject("Z999999").build())
    )

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
        WireMockUtils.withMockGateway(
            stub = getWithBody(statusCode = 200, body = dkifJsonResponse),
            verify = { }
        ) { url ->
            AuthContextHolderThreadLocal.instance().withContext(TEST_SUBJECT) {
                val dkifRestService = DkifServiceRestImpl(url)
                val response = dkifRestService.hentDigitalKontaktinformasjon("06073000250")
                MatcherAssert.assertThat(response.personident, Is.`is`("06073000250"))
                MatcherAssert.assertThat(response.epostadresse?.value, Is.`is`("noreply@nav.no"))
                MatcherAssert.assertThat(response.mobiltelefonnummer?.value, Is.`is`("11111111"))
            }
        }
    }

    @Test
    fun `trigg feil ved henting av kontaktinformasjon fra DkifRestApi`() {
        WireMockUtils.withMockGateway(
            stub = getWithBody(statusCode = 200, body = dkifJsonResponse),
            verify = { }
        ) { url ->
            AuthContextHolderThreadLocal.instance().withContext(TEST_SUBJECT) {
                val dkifRestService = DkifServiceRestImpl(url)
                val response = dkifRestService.hentDigitalKontaktinformasjon("10108000123")
                MatcherAssert.assertThat(response.personident, IsNull())
                MatcherAssert.assertThat(response.reservasjon, Is.`is`(""))
                MatcherAssert.assertThat(response.mobiltelefonnummer?.value, Is.`is`(""))
                MatcherAssert.assertThat(response.epostadresse?.value, Is.`is`(""))
            }
        }
    }

    @Test
    fun `trigg server-feil ved henting av kontaktinformasjon fra DkifRestApi`() {
        WireMockUtils.withMockGateway(
            stub = getWithBody(statusCode = 500, body = null),
            verify = { }
        ) { url ->
            AuthContextHolderThreadLocal.instance().withContext(TEST_SUBJECT) {
                assertThrows<ServerException> {
                    val dkifRestService = DkifServiceRestImpl(url)
                    dkifRestService.hentDigitalKontaktinformasjon("10108000123")
                }
            }
        }
    }
}

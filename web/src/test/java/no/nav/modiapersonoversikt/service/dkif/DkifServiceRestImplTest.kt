package no.nav.modiapersonoversikt.service.dkif

import no.nav.common.auth.subject.IdentType
import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.Subject
import no.nav.common.auth.subject.SubjectHandler
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

    private val TEST_SUBJECT =
        Subject("Z999999", IdentType.InternBruker, SsoToken.oidcToken("token", emptyMap<String, Any>()))

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
            SubjectHandler.withSubject(TEST_SUBJECT) {
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
            SubjectHandler.withSubject(TEST_SUBJECT) {
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
            SubjectHandler.withSubject(TEST_SUBJECT) {
                assertThrows<ServerException> {
                    val dkifRestService = DkifServiceRestImpl(url)
                    dkifRestService.hentDigitalKontaktinformasjon("10108000123")
                }
            }
        }
    }
}

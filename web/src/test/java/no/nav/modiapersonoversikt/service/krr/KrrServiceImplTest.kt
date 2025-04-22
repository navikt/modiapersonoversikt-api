package no.nav.modiapersonoversikt.service.krr

import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import no.nav.modiapersonoversikt.consumer.krr.KrrServiceImpl
import no.nav.modiapersonoversikt.utils.WireMockUtils.json
import no.nav.modiapersonoversikt.utils.WireMockUtils.post
import no.nav.modiapersonoversikt.utils.WireMockUtils.status
import no.nav.personoversikt.common.logging.TjenestekallLogg
import okhttp3.OkHttpClient
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class KrrServiceImplTest {
    private val httpClient = OkHttpClient()

    companion object {
        @JvmField
        @RegisterExtension
        val wiremock: WireMockExtension = WireMockExtension.newInstance().build()
    }

    @Language("json")
    private val jsonResponse =
        """
        {
          "personer":  {
              "10108000398": {
                  "personident": "10108000398",
                  "aktiv": true,
                  "kanVarsles": false,
                  "reservert": false,
                  "reservasjonOppdatert": "2000-01-01T09:00:00Z",
                  "spraak": "nb",
                  "spraakOppdatert": "2000-01-01T09:00:00Z",
                  "epostadresse": "noreply@nav.no",
                  "epostadresseOppdatert": "2000-01-01T09:00:00Z",
                  "epostadresseVerifisert": "2000-01-01T09:00:00Z",
                  "mobiltelefonnummer": "11111111",
                  "mobiltelefonnummerOppdatert": "2000-01-01T09:00:00Z",
                  "mobiltelefonnummerVerifisert": "2000-01-01T09:00:00Z"
                }
            }
        }
        """.trimIndent()

    @Language("json")
    private val jsonFeilResponse =
        """
        {
            "feil": "fortrolig_adresse"
            
        }
        """.trimIndent()

    @Test
    fun `hent kontaktinformasjon fra KrrRestApi`() {
        wiremock.post {
            status(200)
            json(jsonResponse)
        }
        val krrDirRestService = KrrServiceImpl("http://localhost:${wiremock.port}", httpClient, TjenestekallLogg)
        val response = krrDirRestService.hentDigitalKontaktinformasjon("10108000398")
        assertThat(response.personident).isEqualTo("10108000398")
        assertThat(response.epostadresse?.value).isEqualTo("noreply@nav.no")
        assertThat(response.mobiltelefonnummer?.value).isEqualTo("11111111")
        assertThat(response.reservasjon?.value).isEqualTo(false)
        assertThat(response.reservasjon?.sistOppdatert).isNotNull()
    }

    @Test
    fun `trigg feil ved henting av kontaktinformasjon fra KrrRestApi`() {
        wiremock.post { status(404) }
        val krrDirRestService = KrrServiceImpl("http://localhost:${wiremock.port}", httpClient, TjenestekallLogg)

        val response = krrDirRestService.hentDigitalKontaktinformasjon("10108000123")
        assertThat(response.personident).isNull()
        assertThat(response.reservasjon).isEqualTo(null)
        assertThat(response.mobiltelefonnummer?.value).isEqualTo("")
        assertThat(response.epostadresse?.value).isEqualTo("")
    }

    @Test
    fun `trigg feil ved henting av kontaktinformasjon fra KrrRestApi med feil i response`() {
        wiremock.post {
            status(200)
            json(jsonFeilResponse)
        }
        val krrDirRestService = KrrServiceImpl("http://localhost:${wiremock.port}", httpClient, TjenestekallLogg)

        val response = krrDirRestService.hentDigitalKontaktinformasjon("10108000123")
        assertThat(response.personident).isNull()
        assertThat(response.reservasjon).isEqualTo(null)
        assertThat(response.mobiltelefonnummer?.value).isEqualTo("")
        assertThat(response.epostadresse?.value).isEqualTo("")
    }

    @Test
    fun `trigg server-feil ved henting av kontaktinformasjon fra KrrRestApi`() {
        wiremock.post { status(500) }
        val krrDirRestService = KrrServiceImpl("http://localhost:${wiremock.port}", httpClient, TjenestekallLogg)
        val response = krrDirRestService.hentDigitalKontaktinformasjon("10108000123")
        assertThat(response.personident).isNull()
        assertThat(response.reservasjon).isEqualTo(null)
        assertThat(response.mobiltelefonnummer?.value).isEqualTo("")
        assertThat(response.epostadresse?.value).isEqualTo("")
    }
}

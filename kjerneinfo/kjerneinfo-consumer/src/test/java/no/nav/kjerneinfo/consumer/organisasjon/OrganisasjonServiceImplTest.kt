package no.nav.kjerneinfo.consumer.organisasjon

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Rule
import org.junit.Test


class OrganisasjonServiceImplTest {
    @Rule
    @JvmField
    val wiremock = WireMockRule(0)

    private val gyldigRespons = "{\"organisasjonsnummer\": \"orgnr\", \"navn\": { \"navnelinje2\": \"NAV\", \"navnelinje5\": \"IT\" }}"

    @Test
    fun `håndterer 200 statusCode med data`() {
        withMockServer(statusCode = 200, body = gyldigRespons) { service ->
            val nokkelInfo = service.hentNoekkelinfo("any")
            assertThat(nokkelInfo.isPresent, `is`(true))
            assertThat(nokkelInfo.get().navn, `is`("NAV IT"))
        }
    }

    @Test
    fun `håndterer ukjent json uten alvorlig feil`() {
        withMockServer(statusCode = 200, body = "{\"json\": true}") { service ->
            assertThat(service.hentNoekkelinfo("any").isEmpty, `is`(true))
        }
    }

    @Test
    fun `håndterer status coder utenfor 200-299 rangen`() {
        withMockServer(statusCode = 404, body = gyldigRespons) { service ->
            assertThat(service.hentNoekkelinfo("any").isEmpty, `is`(true))
        }

        withMockServer(statusCode = 500) { service ->
            assertThat(service.hentNoekkelinfo("any").isEmpty, `is`(true))
        }
    }

    internal fun withMockServer(statusCode: Int = 200, body: String? = null, test: (OrganisasjonService) -> Unit) {
        stubFor(get(anyUrl())
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)))

        val client = OrganisasjonV1ClientImpl("http://localhost:${wiremock.port()}/")
        val service = OrganisasjonServiceImpl(client)
        test(service)
    }
}

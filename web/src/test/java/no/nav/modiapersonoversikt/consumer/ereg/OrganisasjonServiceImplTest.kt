package no.nav.modiapersonoversikt.consumer.ereg

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.github.tomakehurst.wiremock.matching.AnythingPattern
import no.nav.modiapersonoversikt.config.AppConstants
import no.nav.modiapersonoversikt.infrastructure.RestConstants
import no.nav.personoversikt.test.testenvironment.TestEnvironmentRule
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.junit.Rule
import org.junit.Test

class OrganisasjonServiceImplTest {
    @Rule
    @JvmField
    val wiremock = WireMockRule(0)

    @Rule
    @JvmField
    val testenvironment = TestEnvironmentRule(
        mapOf(
            AppConstants.SYSTEMUSER_USERNAME_PROPERTY to "username"
        )
    )

    private val gyldigRespons = "{\"organisasjonsnummer\": \"orgnr\", \"navn\": { \"navnelinje2\": \"NAV\", \"navnelinje5\": \"IT\" }}"
    private val orgNr = "000000000"

    @Test
    fun `håndterer 200 statusCode med data`() {
        withMockServer(statusCode = 200, body = gyldigRespons) { service ->
            val nokkelInfo = service.hentNoekkelinfo(orgNr)
            MatcherAssert.assertThat(nokkelInfo.isPresent, Is.`is`(true))
            MatcherAssert.assertThat(nokkelInfo.get().navn, Is.`is`("NAV IT"))
        }
    }

    @Test
    fun `håndterer ukjent json uten alvorlig feil`() {
        withMockServer(statusCode = 200, body = "{\"json\": true}") { service ->
            MatcherAssert.assertThat(service.hentNoekkelinfo(orgNr).isEmpty, Is.`is`(true))
        }
    }

    @Test
    fun `håndterer status coder utenfor 200-299 rangen`() {
        withMockServer(statusCode = 404, body = gyldigRespons) { service ->
            MatcherAssert.assertThat(service.hentNoekkelinfo(orgNr).isEmpty, Is.`is`(true))
        }

        withMockServer(statusCode = 500) { service ->
            MatcherAssert.assertThat(service.hentNoekkelinfo(orgNr).isEmpty, Is.`is`(true))
        }
    }

    private fun withMockServer(statusCode: Int = 200, body: String? = null, test: (OrganisasjonService) -> Unit) {
        WireMock.stubFor(
            WireMock.get(WireMock.anyUrl())
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)
                )
        )

        val client = OrganisasjonV1ClientImpl("http://localhost:${wiremock.port()}/")
        val service = OrganisasjonServiceImpl(client)
        test(service)

        WireMock.verify(
            WireMock.getRequestedFor(WireMock.urlEqualTo("/api/v1/organisasjon/$orgNr/noekkelinfo"))
                .withHeader(RestConstants.NAV_CALL_ID_HEADER, AnythingPattern())
                .withHeader(RestConstants.NAV_CONSUMER_ID_HEADER, WireMock.matching(AppConstants.SYSTEMUSER_USERNAME))
                .withHeader("accept", WireMock.matching("application/json"))
        )
    }
}

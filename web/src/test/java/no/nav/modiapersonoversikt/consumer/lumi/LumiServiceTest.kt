package no.nav.modiapersonoversikt.consumer.lumi

import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.matching
import com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import no.nav.modiapersonoversikt.utils.WireMockUtils.post
import no.nav.modiapersonoversikt.utils.WireMockUtils.status
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class LumiServiceTest {
    companion object {
        @JvmField
        @RegisterExtension
        val wiremock: WireMockExtension = WireMockExtension.newInstance().build()

        private val payload =
            com.fasterxml.jackson.module.kotlin.jacksonObjectMapper().readTree(
                """{"surveyId":"survey-1","answers":[{"question":"q1","value":"yes"}]}""",
            )
    }

    private val httpClient =
        OkHttpClient()
            .newBuilder()
            .addInterceptor(XCorrelationIdInterceptor())
            .addInterceptor(AuthorizationInterceptor { "obo-token" })
            .build()

    private val service = LumiServiceImpl("http://localhost:${wiremock.port}", httpClient)

    @Test
    fun `sender transport payload til lumi azure endpoint`() {
        wiremock.post { status(204) }

        service.submitFeedback(payload)

        wiremock.verify(
            postRequestedFor(urlEqualTo("/api/azure/v1/feedback"))
                .withHeader("Authorization", equalTo("Bearer obo-token"))
                .withHeader("X-Correlation-ID", matching(".+"))
                .withRequestBody(equalTo(payload.toString())),
        )
    }

    @Test
    fun `kaster feil naar lumi svarer med error`() {
        wiremock.post { status(500) }

        assertThrows(IllegalStateException::class.java) {
            service.submitFeedback(payload)
        }
    }
}

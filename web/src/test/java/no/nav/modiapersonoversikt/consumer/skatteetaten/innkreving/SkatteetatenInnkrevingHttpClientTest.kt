package no.nav.modiapersonoversikt.consumer.skatteetaten.innkreving

import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.marcinziolo.kotlin.wiremock.*
import io.mockk.every
import io.mockk.mockk
import no.nav.common.rest.client.RestClient
import no.nav.modiapersonoversikt.infrastructure.http.maskinporten.MaskinportenClient
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.test.assertTrue

class SkatteetatenInnkrevingHttpClientTest {
    companion object {
        @RegisterExtension
        val wm: WireMockExtension = WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build()
    }

    private val skatteetatenInnkrevingConfig = SkatteetatenInnkrevingConfig()

    private val maskinportenClient = mockk<MaskinportenClient>()
    private val httpClient = skatteetatenInnkrevingConfig.httpClient(RestClient.baseClient(), maskinportenClient)
    private val apiClient = skatteetatenInnkrevingConfig.apiClient(wm.baseUrl(), httpClient)
    private val kravdetaljerApi = skatteetatenInnkrevingConfig.kravdetaljerApi(apiClient)

    private val skatteetatenInnkrevingClient = SkatteetatenInnkrevingHttpClient(kravdetaljerApi)

    @Test
    fun `get kravdetaljer with auth header should return successfull result`() {
        every { maskinportenClient.getAccessToken() } returns "token"

        wm.get {
            urlPath like "/api/innkreving/innkrevingsoppdrag/v1/innkrevingsoppdrag/.*"
            queryParams contains "kravidentifikatortype"
            headers contains "Authorization" like "Bearer .*"
            headers contains "Accept" equalTo "application/json"
            headers contains "Klientid"
        } returns {
            statusCode = 200
        }

        val result = skatteetatenInnkrevingClient.getKravdetaljer(
            "kravidentifikator",
            KravidentifikatorType.SKATTEETATENS_KRAVIDENTIFIKATOR
        )

        assertTrue(result.isSuccess)
    }

    @Test
    fun `get kravdetaljer should return error when request fails`() {
        every { maskinportenClient.getAccessToken() } returns "token"

        wm.get {
            urlPath like "/api/innkreving/innkrevingsoppdrag/v1/innkrevingsoppdrag/.*"
        } returns {
            statusCode = 500
        }

        val result = skatteetatenInnkrevingClient.getKravdetaljer(
            "kravidentifikator",
            KravidentifikatorType.SKATTEETATENS_KRAVIDENTIFIKATOR
        )

        assertTrue(result.isFailure)
    }
}
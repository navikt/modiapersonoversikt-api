package no.nav.modiapersonoversikt.consumer.skatteetaten.innkreving

import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.marcinziolo.kotlin.wiremock.contains
import com.marcinziolo.kotlin.wiremock.equalTo
import com.marcinziolo.kotlin.wiremock.get
import com.marcinziolo.kotlin.wiremock.like
import com.marcinziolo.kotlin.wiremock.returns
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import no.nav.modiapersonoversikt.config.interceptor.TjenestekallLoggingInterceptorFactory
import no.nav.modiapersonoversikt.consumer.skatteetaten.innkreving.api.generated.models.Kravlinje
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.maskinporten.MaskinportenClient
import no.nav.modiapersonoversikt.service.skatteetaten.innkreving.Grunnlag
import no.nav.modiapersonoversikt.service.skatteetaten.innkreving.Innkrevingskrav
import no.nav.modiapersonoversikt.service.skatteetaten.innkreving.InnkrevingskravId
import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.personoversikt.common.logging.TjenestekallLogg
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
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
    private val unleashService = mockk<UnleashService>()
    private val loggingInterceptor =
        LoggingInterceptor(unleashService, TjenestekallLogg, "SkatteetatenInnkrevingHttpClient") { _ -> "" }
    private val tjenestekallLoggingInterceptorFactory: TjenestekallLoggingInterceptorFactory =
        { _, _ -> loggingInterceptor }

    private val httpClient =
        skatteetatenInnkrevingConfig.httpClient(maskinportenClient, tjenestekallLoggingInterceptorFactory)
    private val apiClient = skatteetatenInnkrevingConfig.apiClient(wm.baseUrl(), httpClient)
    private val kravdetaljerApi = skatteetatenInnkrevingConfig.kravdetaljerApi(apiClient)

    private val skatteetatenInnkrevingClient =
        SkatteetatenHttpInnkrevingskravClient(kravdetaljerApi, "NAV/1.0", unleashService)

    private val iDag = Clock.System.todayIn(TimeZone.currentSystemDefault())

    // language=json
    private val kravdetaljerJson =
        """
        {
            "kravgrunnlag": {
                "datoNaarKravVarBesluttetHosOppdragsgiver": "$iDag"
            },
            "kravlinjer": [
                {
                    "kravlinjetype": "kravType",
                    "opprinneligBeloep": 200.0,
                    "gjenstaaendeBeloep": 100.0
                }
            ]
        }
        
        """.trimIndent()

    @BeforeEach
    fun setup() {
        every { unleashService.isEnabled(Feature.LOG_REQUEST_BODY) } returns false
        every { unleashService.isEnabled(Feature.LOG_RESPONSE_BODY) } returns false
    }

    @Test
    fun `get kravdetaljer with auth header should return successfull result`() {
        every { maskinportenClient.getAccessToken() } returns "token"
        val innkrevingskrav =
            Innkrevingskrav(
                Grunnlag(iDag),
                listOf(
                    Kravlinje(
                        "kravType",
                        200.0,
                        100.0,
                    ),
                ),
            )

        wm.get {
            urlPath like "/api/innkreving/innkrevingsoppdrag/v1/innkrevingsoppdrag/.*"
            queryParams contains "kravidentifikatortype"
            headers contains "Authorization" like "Bearer .*"
            headers contains "Accept" equalTo "application/json"
            headers contains "Klientid"
        } returns {
            header = "Content-Type" to "application/json"
            statusCode = 200
            body = kravdetaljerJson
        }

        val result =
            skatteetatenInnkrevingClient.hentInnkrevingskrav(
                InnkrevingskravId("kravidentifikator"),
            )

        assertThat(result).isNotNull.isEqualTo(innkrevingskrav)
    }

    @Test
    fun `get kravdetaljer should return null when request fails`() {
        every { maskinportenClient.getAccessToken() } returns "token"

        wm.get {
            urlPath like "/api/innkreving/innkrevingsoppdrag/v1/innkrevingsoppdrag/.*"
        } returns {
            statusCode = 500
        }

        val result =
            skatteetatenInnkrevingClient.hentInnkrevingskrav(
                InnkrevingskravId("kravidentifikator"),
            )

        assertThat(result).isNull()
    }

    @Test
    fun `ping should return healthy when feature is enabled`() {
        every { unleashService.isEnabled(Feature.SKATTEETATEN_INNKREVING_API) } returns true
        every { maskinportenClient.getAccessToken() } returns "token"

        wm.get {
            urlPath like "/api/innkreving/innkrevingsoppdrag/v1/innkrevingsoppdrag/.*"
            queryParams contains "kravidentifikatortype"
            headers contains "Authorization" like "Bearer .*"
            headers contains "Accept" equalTo "application/json"
            headers contains "Klientid"
        } returns {
            header = "Content-Type" to "application/json"
            statusCode = 200
            body = kravdetaljerJson
        }

        val result = skatteetatenInnkrevingClient.ping()

        assertTrue(result.check.checkHealth().isHealthy)
    }

    @Test
    fun `ping should return unhealthy when feature is disabled`() {
        every { unleashService.isEnabled(Feature.SKATTEETATEN_INNKREVING_API) } returns false

        val result = skatteetatenInnkrevingClient.ping()

        assertTrue(result.check.checkHealth().isUnhealthy)
    }
}

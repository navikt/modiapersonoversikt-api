package no.nav.modiapersonoversikt.consumer.skatteetaten.innkreving

import no.nav.common.rest.client.RestClient
import no.nav.modiapersonoversikt.config.interceptor.TjenestekallLoggingInterceptorFactory
import no.nav.modiapersonoversikt.consumer.skatteetaten.innkreving.api.generated.apis.KravdetaljerApi
import no.nav.modiapersonoversikt.consumer.skatteetaten.innkreving.api.generated.infrastructure.ApiClient
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.maskinporten.MaskinportenClient
import no.nav.modiapersonoversikt.service.skatteetaten.innkreving.InnkrevingskravClient
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SkatteetatenInnkrevingConfig {
    @Bean("skatteetatenOppdragsinnkrevingClient")
    open fun httpClient(
        maskinportenClient: MaskinportenClient,
        tjenestekallLoggingInterceptorFactory: TjenestekallLoggingInterceptorFactory,
    ): OkHttpClient =
        RestClient
            .baseClient()
            .newBuilder()
            .addInterceptor(XCorrelationIdInterceptor())
            .addInterceptor(
                tjenestekallLoggingInterceptorFactory("SkatteetatenInnkreving") { request ->
                    requireNotNull(request.header("X-Correlation-ID")) {
                        "Kall uten \"X-Correlation-ID\" er ikke lov"
                    }
                },
            ).addInterceptor { chain ->
                val maskinportenToken = maskinportenClient.getAccessToken()

                val request =
                    chain
                        .request()
                        .newBuilder()
                        .addHeader("Authorization", "Bearer $maskinportenToken")
                        .build()

                chain.proceed(request)
            }.build()

    @Bean
    open fun apiClient(
        @Value("\${SKATTEETATEN_INNKREVINGSOPPDRAG_API_BASE_URL}") basePath: String,
        @Qualifier("skatteetatenOppdragsinnkrevingClient") httpClient: OkHttpClient,
    ): ApiClient = ApiClient(basePath, httpClient)

    @Bean
    open fun kravdetaljerApi(apiClient: ApiClient): KravdetaljerApi = KravdetaljerApi(apiClient)

    @Bean
    open fun skatteetatenInnkrevingClient(
        kravdetaljerApi: KravdetaljerApi,
        @Value("\${skatteetaten.api.client.id}") clientId: String,
        unleashService: UnleashService,
    ): InnkrevingskravClient =
        SkatteetatenHttpInnkrevingskravClient(
            kravdetaljerApi = kravdetaljerApi,
            clientId = clientId,
            unleashService = unleashService,
        )
}

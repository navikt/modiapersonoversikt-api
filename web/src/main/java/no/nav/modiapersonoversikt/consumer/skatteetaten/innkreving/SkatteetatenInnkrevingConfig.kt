package no.nav.modiapersonoversikt.consumer.skatteetaten.innkreving

import no.nav.modiapersonoversikt.consumer.skatteetaten.innkreving.api.generated.apis.KravdetaljerApi
import no.nav.modiapersonoversikt.consumer.skatteetaten.innkreving.api.generated.infrastructure.ApiClient
import no.nav.modiapersonoversikt.infrastructure.http.maskinporten.MaskinportenClient
import no.nav.modiapersonoversikt.service.skatteetaten.innkreving.SkatteetatenInnkrevingClient
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
        httpClient: OkHttpClient,
        maskinportenClient: MaskinportenClient,
    ): OkHttpClient =
        httpClient
            .newBuilder()
            .addInterceptor { chain ->
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
    ): SkatteetatenInnkrevingClient =
        SkatteetatenInnkrevingHttpClient(
            kravdetaljerApi = kravdetaljerApi,
            clientId = clientId,
            unleashService = unleashService,
        )
}

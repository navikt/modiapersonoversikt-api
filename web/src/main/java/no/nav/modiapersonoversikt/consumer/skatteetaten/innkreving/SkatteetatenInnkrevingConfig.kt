package no.nav.modiapersonoversikt.consumer.skatteetaten.innkreving

import no.nav.common.rest.client.RestClient
import no.nav.modiapersonoversikt.consumer.ske.oppdragsinnkreving.api.generated.apis.KravdetaljerApi
import no.nav.modiapersonoversikt.consumer.ske.oppdragsinnkreving.api.generated.infrastructure.ApiClient
import no.nav.modiapersonoversikt.infrastructure.http.maskinporten.MaskinportenClient
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SkatteetatenInnkrevingConfig(
    @Autowired private val maskinportenClient: MaskinportenClient,
) {
    @Bean("skeOppdragsinnkrevingClient")
    open fun httpClient(): OkHttpClient = RestClient.baseClientBuilder()
        .addInterceptor { chain ->
            val maskinportenToken = maskinportenClient.getAccessToken()

            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $maskinportenToken")
                .build()

            chain.proceed(request)
        }.build()

    @Bean
    open fun apiClient(
        @Value("\${REST_SKE_OPPDRAGSINNKREVING_BASEURL}")
        basePath: String,
        @Autowired
        @Qualifier("skeOppdragsinnkrevingClient")
        httpClient: OkHttpClient
    ): ApiClient = ApiClient(basePath, httpClient)

    @Bean
    open fun innkrevingsoppdragApi(@Autowired apiClient: ApiClient): KravdetaljerApi =
        KravdetaljerApi(apiClient)
}

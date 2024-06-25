package no.nav.modiapersonoversiktproxy.config

import no.nav.common.rest.client.RestClient
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
open class HttpClientConfig {
    @Bean
    @Primary
    open fun okHttpClient(): OkHttpClient {
        return RestClient.baseClient()
    }
}

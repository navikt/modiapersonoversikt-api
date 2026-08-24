package no.nav.modiapersonoversikt.config

import no.nav.modiapersonoversikt.infrastructure.http.OkHttpUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class ObjectMapperConfig {
    @Bean
    open fun objectMapper() = OkHttpUtils.objectMapper
}

package no.nav.modiapersonoversikt.config

import no.nav.modiapersonoversikt.infrastructure.http.OkHttpUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
open class ObjectMapperConfig {
    @Bean
    @Primary
    open fun objectMapper() = OkHttpUtils.objectMapper
}

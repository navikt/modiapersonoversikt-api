package no.nav.modiapersonoversikt.config

import com.fasterxml.jackson.datatype.joda.JodaModule
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Bean
fun jackson2ObjectMapperBuilder(): Jackson2ObjectMapperBuilderCustomizer {
    return Jackson2ObjectMapperBuilder().modules(JodaModule()).build()
}

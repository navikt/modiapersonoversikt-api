package no.nav.modiapersonoversikt.rest

import org.springframework.context.annotation.Configuration
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.format.datetime.DateFormatter
import org.springframework.format.datetime.DateFormatterRegistrar
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar
import org.springframework.format.support.DefaultFormattingConversionService
import org.springframework.format.support.FormattingConversionService
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport

@Configuration
open class DateTimeParserConfig : WebMvcConfigurationSupport() {
    override fun mvcConversionService(): FormattingConversionService {
        val service = DefaultFormattingConversionService()
        DateTimeFormatterRegistrar().apply {
            setUseIsoFormat(true)
            registerFormatters(service)
        }
        DateFormatterRegistrar().apply {
            val formatter = DateFormatter()
            formatter.setIso(DateTimeFormat.ISO.DATE)
            setFormatter(formatter)
        }
        DateFormatter()
        return service
    }
}

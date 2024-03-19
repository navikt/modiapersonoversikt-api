package no.nav.modiapersonoversikt.consumer.saf.converters

import com.expediagroup.graphql.client.converter.ScalarConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val formatter = DateTimeFormatter.ISO_DATE_TIME

class DateTimeScalarConverter : ScalarConverter<LocalDateTime> {
    override fun toJson(value: LocalDateTime): String = value.format(formatter)

    override fun toScalar(rawValue: Any): LocalDateTime = LocalDateTime.parse(rawValue as String, formatter)
}

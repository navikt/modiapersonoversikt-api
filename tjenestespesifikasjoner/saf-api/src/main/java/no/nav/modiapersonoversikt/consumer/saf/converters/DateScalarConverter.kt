package no.nav.modiapersonoversikt.consumer.saf.converters

import com.expediagroup.graphql.client.converter.ScalarConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val formatter = DateTimeFormatter.ISO_DATE

class DateScalarConverter : ScalarConverter<LocalDate> {
    override fun toJson(value: LocalDate): String = value.format(formatter)

    override fun toScalar(rawValue: Any): LocalDate = LocalDate.parse(rawValue as String, formatter)
}

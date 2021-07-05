package no.nav.modiapersonoversikt.legacy.api.utils.graphqlscalars

import com.expediagroup.graphql.client.converter.ScalarConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val formatter = DateTimeFormatter.ISO_DATE_TIME
class DateTimeScalarConverter : ScalarConverter<LocalDateTime> {
    override fun toJson(value: LocalDateTime): String = value.format(formatter)
    override fun toScalar(rawValue: String): LocalDateTime = LocalDateTime.parse(rawValue, formatter)
}

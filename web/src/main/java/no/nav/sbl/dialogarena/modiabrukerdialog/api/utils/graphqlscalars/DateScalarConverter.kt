package no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.graphqlscalars

import com.expediagroup.graphql.client.converter.ScalarConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val formatter = DateTimeFormatter.ISO_DATE
class DateScalarConverter : ScalarConverter<LocalDate> {
    override fun toJson(value: LocalDate): String = value.format(formatter)
    override fun toScalar(rawValue: String): LocalDate = LocalDate.parse(rawValue, formatter)
}

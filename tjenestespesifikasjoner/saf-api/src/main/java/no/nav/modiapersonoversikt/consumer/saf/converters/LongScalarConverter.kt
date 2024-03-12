package no.nav.modiapersonoversikt.consumer.saf.converters

import com.expediagroup.graphql.client.converter.ScalarConverter

class LongScalarConverter : ScalarConverter<Long> {
    override fun toJson(value: Long): String = value.toString()

    override fun toScalar(rawValue: Any): Long = (rawValue as String).toLong()
}

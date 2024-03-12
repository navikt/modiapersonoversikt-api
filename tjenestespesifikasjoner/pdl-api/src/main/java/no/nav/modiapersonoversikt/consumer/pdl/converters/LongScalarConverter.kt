package no.nav.modiapersonoversikt.consumer.pdl.converters

import com.expediagroup.graphql.client.converter.ScalarConverter

class LongScalarConverter : ScalarConverter<Long> {
    override fun toJson(value: Long): String = value.toString()

    override fun toScalar(rawValue: String): Long = rawValue.toLong()
}

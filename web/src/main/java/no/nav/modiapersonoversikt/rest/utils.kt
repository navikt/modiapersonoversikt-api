package no.nav.modiapersonoversikt.rest

import no.nav.modiapersonoversikt.infrastructure.core.exception.ApplicationException
import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain.Periode
import org.joda.time.IllegalFieldValueException
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import java.time.format.DateTimeFormatter
import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar

const val DATOFORMAT = "yyyy-MM-dd"
const val DATO_TID_FORMAT = "yyyy-MM-dd HH:mm:ss"

fun <K, V> mapOfNotNullOrEmpty(vararg pairs: Pair<K, V>) = pairs
    .filterNot { it.second == null }
    .filterNot { it.second is Map<*, *> && (it.second as Map<*, *>).isEmpty() }
    .filterNot { it.second is Collection<*> && (it.second as Collection<*>).isEmpty() }
    .toMap()

fun lagPeriode(periode: Periode) = mapOf(
    "fra" to periode.from?.toString(DATOFORMAT),
    "til" to periode.to?.toString(DATOFORMAT)
)

fun lagPleiepengePeriode(periode: no.nav.modiapersonoversikt.consumer.infotrygd.domain.pleiepenger.Periode) = mapOf(
    "fom" to periode.fraOgMed?.format(DateTimeFormatter.ofPattern(DATOFORMAT)),
    "tom" to periode.tilOgMed?.format(DateTimeFormatter.ofPattern(DATOFORMAT))
)

fun lagRiktigDato(dato: String?): LocalDate? = dato?.let {
    try {
        LocalDate.parse(dato, DateTimeFormat.forPattern(DATOFORMAT))
    } catch (exception: IllegalFieldValueException) {
        throw ApplicationException(exception.message)
    }
}

fun lagXmlGregorianDato(dato: String?): XMLGregorianCalendar? {
    if (dato == null) {
        return null
    }

    return DatatypeFactory.newInstance().newXMLGregorianCalendar(dato)
}

package no.nav.modiapersonoversikt.commondomain

import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import java.io.Serializable

data class Periode(
    var from: LocalDate? = null,
    var to: LocalDate? = null,
) : Serializable {
    constructor(from: LocalDateTime, toan: LocalDateTime) : this() {
        this.from = LocalDate(from)
        this.to = LocalDate(toan)
    }

    fun erGyldig(): Boolean {
        val today = LocalDate()

        return when {
            from == null -> false
            to == null -> today.isEqual(from) || today.isAfter(from)
            else ->
                (today.isEqual(from) || today.isAfter(from)) &&
                    (today.isEqual(to) || today.isBefore(to))
        }
    }
}

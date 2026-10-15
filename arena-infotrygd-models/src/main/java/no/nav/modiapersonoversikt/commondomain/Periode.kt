package no.nav.modiapersonoversikt.commondomain

import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

data class Periode(
    var from: LocalDate? = null,
    var to: LocalDate? = null,
) : Serializable {
    constructor(from: LocalDateTime, toan: LocalDateTime) : this() {
        this.from = from.toLocalDate()
        this.to = toan.toLocalDate()
    }

    fun erGyldig(): Boolean {
        val today = LocalDate.now()

        return when {
            from == null -> false
            to == null -> today.isEqual(from) || today.isAfter(from)
            else ->
                (today.isEqual(from) || today.isAfter(from)) &&
                    (today.isEqual(to) || today.isBefore(to))
        }
    }
}

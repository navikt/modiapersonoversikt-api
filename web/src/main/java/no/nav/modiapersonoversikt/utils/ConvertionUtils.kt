package no.nav.modiapersonoversikt.utils

import java.time.LocalDate

object ConvertionUtils {
    fun LocalDate.toJodaTime(): org.joda.time.LocalDate {
        return org.joda.time.LocalDate(this.year, this.monthValue, this.dayOfMonth)
    }
    fun org.joda.time.LocalDate.toJavaTime(): LocalDate {
        return LocalDate.of(this.year, this.monthOfYear, this.dayOfMonth)
    }
}

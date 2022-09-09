package no.nav.modiapersonoversikt.utils

import java.time.LocalDate

object ConvertionUtils {
    @JvmStatic
    fun LocalDate.toJodaTime(): org.joda.time.LocalDate {
        return org.joda.time.LocalDate(this.year, this.monthValue, this.dayOfMonth)
    }

    @JvmStatic
    fun org.joda.time.LocalDate.toJavaTime(): LocalDate {
        return LocalDate.of(this.year, this.monthOfYear, this.dayOfMonth)
    }

    fun org.joda.time.DateTime.toJavaTime(): LocalDate {
        return this.toLocalDate().toJavaTime()
    }
}

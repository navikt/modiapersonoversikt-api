package no.nav.modiapersonoversikt.utils

import java.time.LocalDate
import java.time.LocalDateTime

object ConvertionUtils {
    @JvmStatic
    fun LocalDate.toJodaTime(): org.joda.time.LocalDate {
        return org.joda.time.LocalDate(this.year, this.monthValue, this.dayOfMonth)
    }

    @JvmStatic
    fun org.joda.time.LocalDate.toJavaTime(): LocalDate {
        return LocalDate.of(this.year, this.monthOfYear, this.dayOfMonth)
    }

    @JvmStatic
    fun org.joda.time.LocalDateTime.toJavaDateTime(): LocalDateTime {
        return LocalDateTime.of(this.year, this.monthOfYear, this.dayOfMonth, this.hourOfDay, this.minuteOfHour, this.secondOfMinute)
    }

    fun org.joda.time.DateTime.toJavaTime(): LocalDate {
        return this.toLocalDate().toJavaTime()
    }

    fun org.joda.time.DateTime.toJavaDateTime(): LocalDateTime {
        return this.toLocalDateTime().toJavaDateTime()
    }
}

package no.nav.modiapersonoversikt.utils

import no.bekk.bekkopen.date.NorwegianDateUtil
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

object ArbeidsdagerUtils {
    fun arbeidsdagerFraDatoJava(
        ukedager: Int,
        startDato: LocalDate,
    ): LocalDate {
        val zone = ZoneId.systemDefault()
        val date = Date.from(startDato.atStartOfDay(zone).toInstant())
        val future = NorwegianDateUtil.addWorkingDaysToDate(date, ukedager)
        return future
            .toInstant()
            .atZone(zone)
            .toLocalDate()
    }
}

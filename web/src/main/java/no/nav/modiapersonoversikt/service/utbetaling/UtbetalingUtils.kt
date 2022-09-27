package no.nav.modiapersonoversikt.service.utbetaling

import java.time.LocalDate

object UtbetalingUtils {
    val EKSTRA_SOKEPERIODE: Long = 20

    @JvmStatic
    fun leggTilEkstraDagerPaaStartdato(dato: LocalDate): LocalDate {
        val tidligsteDato = LocalDate.now().minusYears(3).withDayOfYear(1)
        val nyStartDato = dato.minusDays(EKSTRA_SOKEPERIODE)
        return if (nyStartDato < tidligsteDato) tidligsteDato else nyStartDato
    }
}

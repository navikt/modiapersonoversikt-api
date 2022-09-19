package no.nav.modiapersonoversikt.service.utbetaling

import no.nav.modiapersonoversikt.utils.ConvertionUtils.toJavaTime
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSUtbetaling
import java.time.LocalDate
import java.util.function.Predicate

object UtbetalingUtils {
    val EKSTRA_SOKEPERIODE: Long = 20

    @JvmStatic
    fun finnUtbetalingerISokeperioden(start: LocalDate, slutt: LocalDate) = Predicate<WSUtbetaling> { utbetaling ->
        val dato = listOfNotNull(
            utbetaling.utbetalingsdato,
            utbetaling.forfallsdato,
            utbetaling.posteringsdato,
        ).firstOrNull()?.toJavaTime()

        erDatoISokeperioden(dato, start, slutt)
    }

    @JvmStatic
    fun leggTilEkstraDagerPaaStartdato(dato: LocalDate): LocalDate {
        val tidligsteDato = LocalDate.now().minusYears(3).withDayOfYear(1)
        val nyStartDato = dato.minusDays(EKSTRA_SOKEPERIODE)
        return if (nyStartDato < tidligsteDato) tidligsteDato else nyStartDato
    }

    private fun erDatoISokeperioden(dato: LocalDate?, start: LocalDate, slutt: LocalDate): Boolean {
        if (dato == null) {
            return false
        }
        return dato in start..slutt
    }
}

package no.nav.modiapersonoversikt.service.utbetaling.domain.util;

import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSUtbetaling;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.function.Predicate;


public class UtbetalingUtils {
    public static Predicate<WSUtbetaling> finnUtbetalingerISokeperioden(final LocalDate startDato, final LocalDate sluttDato) {

        return utbetaling -> {

            DateTime utbetalingsDato = utbetaling.getUtbetalingsdato();
            if (utbetalingsDato != null) {
                return erDatoISokeperioden(utbetalingsDato.toLocalDate(), startDato, sluttDato);
            }
            DateTime forfallsdato = utbetaling.getForfallsdato();
            if (forfallsdato != null) {
                return erDatoISokeperioden(forfallsdato.toLocalDate(), startDato, sluttDato);
            }
            DateTime posteringsdato = utbetaling.getPosteringsdato();

            return posteringsdato != null && erDatoISokeperioden(posteringsdato.toLocalDate(), startDato, sluttDato);
        };
    }

    public static boolean erDatoISokeperioden(LocalDate dato, LocalDate startDato, LocalDate sluttDato) {
        return dato.compareTo(startDato) >= 0 && dato.compareTo(sluttDato) <= 0;
    }
}

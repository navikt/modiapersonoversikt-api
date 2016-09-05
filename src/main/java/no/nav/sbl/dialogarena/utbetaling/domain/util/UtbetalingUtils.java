package no.nav.sbl.dialogarena.utbetaling.domain.util;

import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSUtbetaling;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.function.Predicate;


public class UtbetalingUtils {
    public static Predicate<WSUtbetaling> finnUtbetalingerISokeperioden(final LocalDate startDato, final LocalDate sluttDato) {

        return utbetaling -> {

            DateTime utbetalingsDato = utbetaling.getUtbetalingsdato();
            if (utbetalingsDato != null) {
                return erUtbetalingsdatoISokeperioden(utbetalingsDato.toLocalDate(), startDato, sluttDato);
            }
            DateTime forfallsdato = utbetaling.getForfallsdato();
            if (forfallsdato != null) {
                return erUtbetalingsdatoISokeperioden(forfallsdato.toLocalDate(), startDato, sluttDato);
            }
            DateTime posteringsdato = utbetaling.getPosteringsdato();
            if (posteringsdato != null){
                return erUtbetalingsdatoISokeperioden(posteringsdato.toLocalDate(), startDato, sluttDato);
            }
            return false;
        };
    }

    static boolean erUtbetalingsdatoISokeperioden(LocalDate utbetalingsdato, LocalDate startDato, LocalDate sluttDato) {
        return (utbetalingsdato.compareTo(startDato) >= 0 && utbetalingsdato.compareTo(sluttDato) <= 0);
    }
}

package no.nav.sbl.dialogarena.utbetaling.domain.util;

import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSUtbetaling;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.function.Predicate;


public class UtbetalingUtils {
    public static Predicate<WSUtbetaling> finnUtbetalingerMedUtbetalingsdatoISokeperioden(final LocalDate startDato, final LocalDate sluttDato) {

        return utbetaling -> {

            DateTime utbetalingsDato = utbetaling.getUtbetalingsdato();
            return utbetalingsDato != null && erUtbetalingsdatoISokeperioden(utbetalingsDato.toLocalDate(), startDato, sluttDato);
        };
    }

    static boolean erUtbetalingsdatoISokeperioden(LocalDate utbetalingsdato, LocalDate startDato, LocalDate sluttDato) {
        return (utbetalingsdato.compareTo(startDato) >= 0 && utbetalingsdato.compareTo(sluttDato) <= 0);
    }
}

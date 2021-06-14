package no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.utbetalinger;

import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.domain.utbetalinger.Hovedytelse;
import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.WSUtbetaling;
import org.joda.time.LocalDate;

import java.util.stream.Collectors;


public class UtbetalingUtils {

    static boolean utbetalingInnenforSokeperioden(final WSUtbetaling utbetaling, final LocalDate startDato, final LocalDate sluttDato) {
        if(utbetaling.getUtbetalingsdato() == null) return false;

        LocalDate utbetalingsdato = utbetaling.getUtbetalingsdato().toLocalDate();
        return utbetalingsdato.compareTo(startDato) >= 0
                && utbetalingsdato.compareTo(sluttDato) <= 0;
    }

    public static boolean harGyldigUtbetaling(Hovedytelse hovedytelse) {
        return hovedytelse.getHistoriskUtbetalinger() != null && !hovedytelse.getHistoriskUtbetalinger().isEmpty();
    }

    static Hovedytelse fjernHistoriskUtbetalingerMedFeilUtbetalingsType(Hovedytelse hovedytelse, String utbetalingstype) {
        return new Hovedytelse(hovedytelse)
                .withHistoriskUtbetalinger(hovedytelse.getHistoriskUtbetalinger().stream()
                        .filter(utbetaling -> utbetalingstype.toUpperCase().equals(utbetaling.getType().trim().toUpperCase()))
                        .collect(Collectors.toList()));
    }
}

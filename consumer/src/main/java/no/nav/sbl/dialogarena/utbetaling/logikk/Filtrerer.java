package no.nav.sbl.dialogarena.utbetaling.logikk;

import org.joda.time.LocalDate;

import static no.nav.sbl.dialogarena.utbetaling.domain.Mottaker.ARBEIDSGIVER;
import static no.nav.sbl.dialogarena.utbetaling.domain.Mottaker.BRUKER;


public class Filtrerer {

    public static boolean filtrerPaaDatoer(LocalDate utbetalingsDato, LocalDate startDato, LocalDate sluttDato) {
        return utbetalingsDato.isAfter(startDato) && utbetalingsDato.isBefore(sluttDato);
    }

    public static boolean filtrerPaaMottaker(String mottakerkode, boolean visArbeidsgiver, boolean visBruker) {
        boolean arbeidsgiverVises = visArbeidsgiver && ARBEIDSGIVER.equalsIgnoreCase(mottakerkode);
        boolean brukerVises = visBruker && BRUKER.equalsIgnoreCase(mottakerkode);
        return arbeidsgiverVises || brukerVises;
    }
}

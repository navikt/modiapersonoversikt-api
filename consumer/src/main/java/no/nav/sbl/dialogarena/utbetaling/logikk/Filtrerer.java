package no.nav.sbl.dialogarena.utbetaling.logikk;

import no.nav.sbl.dialogarena.utbetaling.domain.Mottaker;
import org.joda.time.LocalDate;


public class Filtrerer {

    public static boolean filtrerPaaDatoer(LocalDate utbetalingsDato, LocalDate startDato, LocalDate sluttDato) {
        return utbetalingsDato.isAfter(startDato) &&
                utbetalingsDato.isBefore(sluttDato);
    }

    public static boolean filtrerPaaMottaker(String mottakerkode, boolean visArbeidsgiver, boolean visBruker) {
        boolean arbeidsgiverVises = visArbeidsgiver && Mottaker.ARBEIDSGIVER.equalsIgnoreCase(mottakerkode);
        boolean brukerVises = visBruker && Mottaker.BRUKER.equalsIgnoreCase(mottakerkode);
        return arbeidsgiverVises || brukerVises;
    }
}

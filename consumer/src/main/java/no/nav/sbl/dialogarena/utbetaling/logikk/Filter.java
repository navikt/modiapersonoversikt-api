package no.nav.sbl.dialogarena.utbetaling.logikk;

import no.nav.sbl.dialogarena.utbetaling.domain.FilterParametere;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.joda.time.LocalDate;

import static no.nav.sbl.dialogarena.utbetaling.domain.Mottaker.ARBEIDSGIVER;
import static no.nav.sbl.dialogarena.utbetaling.domain.Mottaker.BRUKER;


public class Filter {

    public static boolean filtrer(Utbetaling utbetaling, FilterParametere params) {
        boolean innenforDatoer = Filter.filtrerPaaDatoer(utbetaling.getUtbetalingsDato().toLocalDate(), params.startDato, params.sluttDato);
        boolean brukerSkalVises = Filter.filtrerPaaMottaker(utbetaling.getMottaker().getMottakertypeType(), params.visArbeidsgiver, params.visBruker);
        return innenforDatoer && brukerSkalVises;
    }

    private static boolean filtrerPaaDatoer(LocalDate utbetalingsDato, LocalDate startDato, LocalDate sluttDato) {
        return utbetalingsDato.isAfter(startDato) && utbetalingsDato.isBefore(sluttDato);
    }

    private static boolean filtrerPaaMottaker(String mottakerkode, boolean visArbeidsgiver, boolean visBruker) {
        boolean arbeidsgiverVises = visArbeidsgiver && ARBEIDSGIVER.equalsIgnoreCase(mottakerkode);
        boolean brukerVises = visBruker && BRUKER.equalsIgnoreCase(mottakerkode);
        return arbeidsgiverVises || brukerVises;
    }
}

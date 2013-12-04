package no.nav.sbl.dialogarena.utbetaling.filter;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.joda.time.LocalDate;

import static no.nav.sbl.dialogarena.utbetaling.domain.Mottaker.ARBEIDSGIVER;
import static no.nav.sbl.dialogarena.utbetaling.domain.Mottaker.BRUKER;


public class Filter {

    public static boolean filtrer(Utbetaling utbetaling, FilterParametere filterParametere) {
        boolean innenforDatoer = filtrerPaaDatoer(utbetaling.getUtbetalingsDato().toLocalDate(), filterParametere.getStartDato(), filterParametere.getSluttDato());
        boolean brukerSkalVises = filtrerPaaMottaker(utbetaling.getMottaker().getMottakertypeType(), filterParametere.getVisArbeidsgiver(), filterParametere.getVisBruker());
        return innenforDatoer && brukerSkalVises;
    }

    private static boolean filtrerPaaDatoer(LocalDate utbetalingsDato, LocalDate startDato, LocalDate sluttDato) {
        return utbetalingsDato.isAfter(startDato.minusDays(1)) && utbetalingsDato.isBefore(sluttDato.plusDays(1));
    }

    private static boolean filtrerPaaMottaker(String mottakerkode, boolean visArbeidsgiver, boolean visBruker) {
        boolean arbeidsgiverVises = visArbeidsgiver && ARBEIDSGIVER.equalsIgnoreCase(mottakerkode);
        boolean brukerVises = visBruker && BRUKER.equalsIgnoreCase(mottakerkode);
        return arbeidsgiverVises || brukerVises;
    }
}

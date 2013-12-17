package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.joda.time.LocalDate;

import java.util.Set;

import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.ARBEIDSGIVER;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.BRUKER;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils.hentYtelser;


public class Filter {

    public static boolean filtrer(Utbetaling utbetaling, FilterParametere filterParametere) {
        boolean innenforDatoer = filtrerPaaDatoer(utbetaling.getUtbetalingsDato().toLocalDate(), filterParametere.getStartDato(), filterParametere.getSluttDato());
        boolean brukerSkalVises = filtrerPaaMottaker(utbetaling.mottakertype, filterParametere.getVisArbeidsgiver(), filterParametere.getVisBruker());
        boolean harYtelse = filtrerPaaYtelser(utbetaling, filterParametere.uonskedeYtelser);
        return innenforDatoer && brukerSkalVises && harYtelse;
    }

    private static boolean filtrerPaaDatoer(LocalDate utbetalingsDato, LocalDate startDato, LocalDate sluttDato) {
        return utbetalingsDato.isAfter(startDato.minusDays(1)) && utbetalingsDato.isBefore(sluttDato.plusDays(1));
    }

    private static boolean filtrerPaaMottaker(String mottakerkode, boolean visArbeidsgiver, boolean visBruker) {
        boolean arbeidsgiverVises = visArbeidsgiver && ARBEIDSGIVER.equalsIgnoreCase(mottakerkode);
        boolean brukerVises = visBruker && BRUKER.equalsIgnoreCase(mottakerkode);
        return arbeidsgiverVises || brukerVises;
    }

    private static boolean filtrerPaaYtelser(Utbetaling utbetaling, Set<String> uonskedeYtelser) {
        for (String ytelse : hentYtelser(utbetaling)) {
            if (!uonskedeYtelser.contains(ytelse)) {
                return true;
            }
        }
        return false;
    }

}

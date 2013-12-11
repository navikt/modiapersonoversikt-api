package no.nav.sbl.dialogarena.utbetaling.filter;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils;
import org.joda.time.LocalDate;

import java.util.Arrays;
import java.util.List;

import static no.nav.sbl.dialogarena.utbetaling.domain.Mottaker.ARBEIDSGIVER;
import static no.nav.sbl.dialogarena.utbetaling.domain.Mottaker.BRUKER;


public class Filter {

    public static boolean filtrer(Utbetaling utbetaling, FilterParametere filterParametere) {
        boolean innenforDatoer = filtrerPaaDatoer(utbetaling.getUtbetalingsDato().toLocalDate(), filterParametere.getStartDato(), filterParametere.getSluttDato());
        boolean brukerSkalVises = filtrerPaaMottaker(utbetaling.getMottaker().getMottakertypeType(), filterParametere.getVisArbeidsgiver(), filterParametere.getVisBruker());
        boolean harYtelse = filtrerPaaYtelser(utbetaling, filterParametere.getValgteYtelser());
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

    private static boolean filtrerPaaYtelser(Utbetaling utbetaling, List<FilterParametere.ValgtYtelse> valgteYtelser) {
        List<String> ytelserIUtbetaling = UtbetalingListeUtils.hentYtelserFraUtbetalinger(Arrays.asList(utbetaling));
        for (String ytelse : ytelserIUtbetaling) {
            for (FilterParametere.ValgtYtelse valgtYtelse : valgteYtelser) {
                if(valgtYtelse.getValgt() && ytelse.equalsIgnoreCase(valgtYtelse.getYtelse())) {
                    return true;
                }
            }
        }
        return false;
    }

}

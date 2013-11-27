package no.nav.sbl.dialogarena.utbetaling.logikk;


import no.nav.sbl.dialogarena.utbetaling.domain.Oppsummering;
import no.nav.sbl.dialogarena.utbetaling.domain.Periode;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;

import java.util.ArrayList;
import java.util.List;

public class OppsummeringsKalkulator {

    public static Oppsummering regnUtOppsummering(List<Utbetaling> utbetalinger) {
        Oppsummering oppsummering = new Oppsummering();
        for (Utbetaling utbetaling : utbetalinger) {
            oppsummering.utbetalt += utbetaling.getNettoBelop();
            oppsummering.brutto += utbetaling.getBruttoBelop();
            oppsummering.trekk += utbetaling.getBruttoBelop() - utbetaling.getNettoBelop();
        }
        return oppsummering;
    }


    public static List<Utbetaling> finnUtbetalingerIPeriode(List<Utbetaling> utbetalinger, Periode periode) {
        List<Utbetaling> resultat = new ArrayList<>();
        for (Utbetaling utbetaling : utbetalinger) {
            if(periode.containsDate(utbetaling.getUtbetalingsDato())) {
                resultat.add(utbetaling);
            }
        }

        return resultat;
    }
}

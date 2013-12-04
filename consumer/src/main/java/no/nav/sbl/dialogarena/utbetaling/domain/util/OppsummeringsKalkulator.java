package no.nav.sbl.dialogarena.utbetaling.domain.util;


import no.nav.sbl.dialogarena.utbetaling.domain.Oppsummering;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;

import java.util.List;

public class OppsummeringsKalkulator {

    public static Oppsummering regnUtOppsummering(List<Utbetaling> utbetalinger) {
        Oppsummering oppsummering = new Oppsummering();
        for (Utbetaling utbetaling : utbetalinger) {
            oppsummering.valuta = utbetaling.getValuta();
            oppsummering.utbetalt += utbetaling.getNettoBelop();
            oppsummering.brutto += utbetaling.getBruttoBelop();
            oppsummering.trekk += utbetaling.getTrekk() == 0.0 ?
                    utbetaling.getBruttoBelop() - utbetaling.getNettoBelop() :
                    utbetaling.getTrekk();
        }
        return oppsummering;
    }
}

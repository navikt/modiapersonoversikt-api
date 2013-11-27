package no.nav.sbl.dialogarena.utbetaling.logikk;


import no.nav.sbl.dialogarena.utbetaling.domain.Oppsummering;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;

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

}

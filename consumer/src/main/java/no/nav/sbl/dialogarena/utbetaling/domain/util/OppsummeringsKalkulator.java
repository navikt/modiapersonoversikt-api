package no.nav.sbl.dialogarena.utbetaling.domain.util;


import no.nav.sbl.dialogarena.utbetaling.domain.oppsummering.Oppsummering;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;

import java.util.List;

import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils.summerBelopForUnderytelser;

public class OppsummeringsKalkulator {

    public static Oppsummering regnUtOppsummering(List<Utbetaling> utbetalinger) {
        Oppsummering oppsummering = new Oppsummering();
        for (Utbetaling utbetaling : utbetalinger) {
            oppsummering.utbetalt += utbetaling.getNettoBelop();
            oppsummering.brutto += utbetaling.getBruttoBelop();
            oppsummering.trekk += utbetaling.getTrekk() == 0.0 ?
                    utbetaling.getBruttoBelop() - utbetaling.getNettoBelop() :
                    utbetaling.getTrekk();
        }

        oppsummering.ytelserUtbetalt = summerBelopForUnderytelser(utbetalinger);
        return oppsummering;
    }
}

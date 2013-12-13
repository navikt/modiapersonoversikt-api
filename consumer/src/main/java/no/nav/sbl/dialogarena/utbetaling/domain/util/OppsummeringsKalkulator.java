package no.nav.sbl.dialogarena.utbetaling.domain.util;


import no.nav.sbl.dialogarena.utbetaling.domain.oppsummering.HovedYtelse;
import no.nav.sbl.dialogarena.utbetaling.domain.oppsummering.Oppsummering;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.sort;
import static no.nav.sbl.dialogarena.utbetaling.domain.oppsummering.HovedYtelse.HovedYtelseComparator.NAVN;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils.summerBelopForUnderytelser;

public class OppsummeringsKalkulator {

    public static Oppsummering regnUtOppsummering(List<Utbetaling> utbetalinger) {
        double utbetalt = 0, brutto = 0, trekk = 0;
        for (Utbetaling utbetaling : utbetalinger) {
            utbetalt += utbetaling.getNettoBelop();
            brutto += utbetaling.getBruttoBelop();
            trekk += utbetaling.getTrekk() == 0.0 ?
                    utbetaling.getBruttoBelop() - utbetaling.getNettoBelop() :
                    utbetaling.getTrekk();
        }

        Map<String,Map<String,Double>> ytelserUtbetalt = summerBelopForUnderytelser(utbetalinger);
        return new Oppsummering(utbetalt, trekk, brutto, ytelserUtbetalt);
    }
}

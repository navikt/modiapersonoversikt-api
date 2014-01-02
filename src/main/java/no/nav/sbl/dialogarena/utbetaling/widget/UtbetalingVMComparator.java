package no.nav.sbl.dialogarena.utbetaling.widget;

import java.util.Comparator;

/**
 * Comparator som sorterer i omvendt kronologisk rekkefølge på utbetalingsdato
 */
public class UtbetalingVMComparator implements Comparator<UtbetalingVM> {

    @Override
    public int compare(UtbetalingVM utbetalingVM1, UtbetalingVM utbetalingVM2) {
        if (utbetalingVM1.getUtbetalingsDato() == null && utbetalingVM2.getUtbetalingsDato() == null) {
            return 0;
        }
        if (utbetalingVM1.getUtbetalingsDato() == null) {
            return -1;
        }
        if (utbetalingVM2.getUtbetalingsDato() == null) {
            return 1;
        }
        return utbetalingVM2.getUtbetalingsDato().compareTo(utbetalingVM1.getUtbetalingsDato());
    }
}

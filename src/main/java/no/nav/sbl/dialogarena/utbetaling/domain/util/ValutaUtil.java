package no.nav.sbl.dialogarena.utbetaling.domain.util;

import java.text.NumberFormat;
import java.util.Locale;

import static java.text.NumberFormat.getNumberInstance;


public class ValutaUtil {

    public static String getBelopString(double belop) {
        NumberFormat currencyInstance = getNumberInstance(Locale.getDefault());
        currencyInstance.setMinimumFractionDigits(2);
        return currencyInstance.format(belop);
    }
}

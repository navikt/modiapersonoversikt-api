package no.nav.sbl.dialogarena.utbetaling.domain.util;

import java.text.NumberFormat;

import static java.text.NumberFormat.getNumberInstance;
import static java.util.Locale.forLanguageTag;


public class ValutaUtil {

    public static String getBelopString(double belop) {
        NumberFormat currencyInstance = getNumberInstance(forLanguageTag("nb-no"));
        currencyInstance.setMinimumFractionDigits(2);
        return currencyInstance.format(belop);
    }
}

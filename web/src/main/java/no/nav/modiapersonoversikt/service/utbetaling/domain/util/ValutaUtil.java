package no.nav.modiapersonoversikt.service.utbetaling.domain.util;

import java.text.NumberFormat;
import java.util.Locale;

import static java.text.NumberFormat.getNumberInstance;


public class ValutaUtil {

    public static String getBelopString(double belop) {
        return getBelopString(belop, 2);
    }

    public static String getBelopString(double belop, int minimumFractionDigits) {
        NumberFormat currencyInstance = getNumberInstance(Locale.getDefault());
        currencyInstance.setMinimumFractionDigits(minimumFractionDigits);
        return currencyInstance.format(belop);
    }
}

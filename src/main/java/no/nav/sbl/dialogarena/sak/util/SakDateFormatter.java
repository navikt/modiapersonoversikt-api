package no.nav.sbl.dialogarena.sak.util;

import no.nav.modig.core.exception.ApplicationException;
import org.joda.time.DateTime;

import java.util.Locale;

import static org.joda.time.format.DateTimeFormat.forPattern;

public class SakDateFormatter {

    public static final Locale LOCALE = new Locale("no");

    public static String printShortDate(DateTime dateTime) {
        throwExceptionIfNullDate(dateTime);
        return forPattern("dd.MM.YYYY")
                .withLocale(LOCALE)
                .print(dateTime);
    }

    public static String printLongDate(DateTime dateTime) {
        throwExceptionIfNullDate(dateTime);
        return forPattern("d. MMMM YYYY")
                .withLocale(LOCALE)
                .print(dateTime);
    }

    public static String printFullDate(DateTime dateTime) {
        throwExceptionIfNullDate(dateTime);
        return forPattern("d. MMMM yyyy, HH:mm")
                .withLocale(LOCALE)
                .print(dateTime);
    }

    private static void throwExceptionIfNullDate(DateTime dateTime) {
        if (dateTime == null) {
            throw new ApplicationException("Kunne ikke formatere null-dato");
        }
    }

}

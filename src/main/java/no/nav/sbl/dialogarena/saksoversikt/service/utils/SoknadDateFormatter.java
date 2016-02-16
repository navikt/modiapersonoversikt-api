package no.nav.sbl.dialogarena.saksoversikt.service.utils;

import no.nav.modig.core.exception.ApplicationException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Locale;

public class SoknadDateFormatter {

    public static String printShortDate(DateTime dateTime) {
        throwExceptionIfNullDate(dateTime);
        return DateTimeFormat
                .forPattern("dd.MM.YYYY")
                .withLocale(new Locale("no"))
                .print(dateTime);
    }

    public static String printLongDate(DateTime dateTime) {
        throwExceptionIfNullDate(dateTime);
        return DateTimeFormat
                .forPattern("d. MMMM YYYY")
                .withLocale(new Locale("no"))
                .print(dateTime);
    }

    public static String printTime(DateTime dateTime) {
        throwExceptionIfNullDate(dateTime);
        return DateTimeFormat.forPattern("HH.mm")
                .print(dateTime);
    }

    public static String printFullDate(DateTime dateTime, String klokken) {
        return String.format("%s, %s %s", printLongDate(dateTime), klokken, printTime(dateTime));
    }

    private static void throwExceptionIfNullDate(DateTime dateTime) {
        if (dateTime == null) {
            throw new ApplicationException("Kunne ikke formatere null-dato");
        }
    }

}

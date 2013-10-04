package no.nav.sbl.dialogarena.soknader.liste.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Locale;

public class DateFormatter {

    public static String printShortDate(DateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return DateTimeFormat
                .forPattern("dd.MM.YYYY")
                .withLocale(new Locale("no"))
                .print(dateTime);
    }
}

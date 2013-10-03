package no.nav.sbl.dialogarena.soknader.widget.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Locale;

public class SoknadDateFormatter {

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

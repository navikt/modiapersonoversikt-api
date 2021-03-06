package no.nav.modiapersonoversikt.legacy.sporsmalogsvar.common.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;

import static java.lang.String.format;
import static no.bekk.bekkopen.date.NorwegianDateUtil.addWorkingDaysToDate;

public class DateUtils {

    private static Locale locale = Locale.getDefault();

    public static java.time.LocalDate arbeidsdagerFraDatoJava(int ukedager, java.time.LocalDate startDato) {
        ZoneId zone = ZoneId.systemDefault();
        ZonedDateTime now = startDato.atStartOfDay(zone);
        Date future = addWorkingDaysToDate(Date.from(now.toInstant()), ukedager);
        return future.toInstant()
                .atZone(zone)
                .toLocalDate();
    }

    public static String toDateString(DateTime dateTime) {
        return DateTimeFormat.forPattern("d. MMMM yyyy").withLocale(locale).print(dateTime);
    }

    public static String toTimeString(DateTime dateTime) {
        return DateTimeFormat.forPattern("HH.mm").withLocale(locale).print(dateTime);
    }

    public static String toString(DateTime dateTime) {
        return format("%s, klokken %s", toDateString(dateTime), toTimeString(dateTime));
    }

    public static String date(DateTime dateTime) {
        return DateTimeFormat.forPattern("d. MMMM yyyy").withLocale(Locale.getDefault()).print(dateTime);
    }
}

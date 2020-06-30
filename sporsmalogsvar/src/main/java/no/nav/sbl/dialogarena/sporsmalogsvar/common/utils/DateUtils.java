package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;

import org.apache.commons.collections15.Factory;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;

import static java.lang.String.format;
import static no.bekk.bekkopen.date.NorwegianDateUtil.addWorkingDaysToDate;
import static org.apache.commons.collections15.FactoryUtils.constantFactory;

public class DateUtils {

    private static Factory<Locale> locale = constantFactory(Locale.getDefault());

    public static LocalDate arbeidsdagerFraDato(int ukedager, LocalDate startDato) {
        return LocalDate.fromDateFields(addWorkingDaysToDate(startDato.toDate(), ukedager));
    }


    public static java.time.LocalDate arbeidsdagerFraDatoJava(int ukedager, java.time.LocalDate startDato) {
        ZoneId zone = ZoneId.systemDefault();
        ZonedDateTime now = startDato.atStartOfDay(zone);
        Date future = addWorkingDaysToDate(Date.from(now.toInstant()), ukedager);
        return future.toInstant()
                .atZone(zone)
                .toLocalDate();
    }

    public static String toDateString(DateTime dateTime) {
        return DateTimeFormat.forPattern("d. MMMM yyyy").withLocale(locale.create()).print(dateTime);
    }

    public static String toTimeString(DateTime dateTime) {
        return DateTimeFormat.forPattern("HH.mm").withLocale(locale.create()).print(dateTime);
    }

    public static String toString(DateTime dateTime) {
        return format("%s, klokken %s", toDateString(dateTime), toTimeString(dateTime));
    }

    public static String date(DateTime dateTime) {
        return DateTimeFormat.forPattern("d. MMMM yyyy").withLocale(Locale.getDefault()).print(dateTime);
    }
}

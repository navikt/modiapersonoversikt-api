package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;

import org.apache.commons.collections15.Factory;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.util.Locale;

import static java.lang.String.format;
import static no.bekk.bekkopen.date.NorwegianDateUtil.addWorkingDaysToDate;
import static org.apache.commons.collections15.FactoryUtils.constantFactory;

public class DateUtils {

    private static Factory<Locale> locale = constantFactory(Locale.getDefault());

    public static LocalDate arbeidsdagerFraDato(int ukedager, LocalDate startDato) {
        return LocalDate.fromDateFields(addWorkingDaysToDate(startDato.toDate(), ukedager));
    }

    public static String date(DateTime dateTime) {
        return DateTimeFormat.forPattern("d. MMM. yyyy").withLocale(locale.create()).print(dateTime);
    }

    public static String time(DateTime dateTime) {
        return DateTimeFormat.forPattern("HH.mm").withLocale(locale.create()).print(dateTime);
    }

    public static String dateTime(DateTime dateTime) {
        return format("%s, kl. %s", date(dateTime), time(dateTime));
    }
}

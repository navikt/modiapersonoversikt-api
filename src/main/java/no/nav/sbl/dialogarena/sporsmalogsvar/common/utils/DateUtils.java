package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;

import org.apache.commons.collections15.Factory;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.util.Locale;

import static java.lang.String.format;
import static org.apache.commons.collections15.FactoryUtils.constantFactory;
import static org.joda.time.DateTimeConstants.SATURDAY;
import static org.joda.time.DateTimeConstants.SUNDAY;

public class DateUtils {

    private static Factory<Locale> locale = constantFactory(Locale.getDefault());

    public static LocalDate ukedagerFraDato(int ukedager, LocalDate startDato) {
        LocalDate dato = new LocalDate(startDato);
        for (int dagerIgjen = ukedager; dagerIgjen > 0; dagerIgjen--) {
            dato = dato.plusDays(1);
            while (erHelg(dato)) {
                dato = dato.plusDays(1);
            }
        }
        return dato;
    }

    public static boolean erHelg(LocalDate dato) {
        return dato.getDayOfWeek() == SATURDAY || dato.getDayOfWeek() == SUNDAY;
    }

    public static void useLocaleFrom(Factory<Locale> localeProvider) {
        locale = localeProvider;
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

package no.nav.modiapersonoversikt.legacy.utbetaling.domain.util;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;

public final class DateUtils {

    public static final int EKSTRA_SOKEPERIODE = 20;

    public static final Function<Interval, DateTime> START = interval -> interval.getStart();

    public static final Function<Interval, DateTime> END = interval -> interval.getEnd();

    public static Predicate<LocalDate> isAfter(final LocalDate compare) {
        return localDate -> localDate.isAfter(compare);
    }

    public static DateTime minusDaysAndFixedAtMidnightAtDayBefore(DateTime prevDate, int minusDays) {
        return prevDate.minusDays(minusDays+1).toDateMidnight().toDateTime();
    }

    public static Interval intervalFromStartEndDate(LocalDate startDato, LocalDate sluttDato) {
        return new Interval(startDato.toDateTimeAtStartOfDay(), sluttDato.toDateMidnight().toDateTime().plusDays(1));
    }

    public static boolean isUnixEpoch(DateTime dateTime) {
        return dateTime.getMillis() == 0;
    }

    public static boolean isUnixEpoch(LocalDate localDate) {
        return localDate.getYear() == 1970;
    }

    public static LocalDate leggTilEkstraDagerPaaStartdato(LocalDate startdato){
        LocalDate minsteDato = LocalDate.now().minusYears(3).withDayOfYear(1);
        LocalDate nyStartdato = startdato.minusDays(EKSTRA_SOKEPERIODE);

        return (nyStartdato.compareTo(minsteDato) < 0) ? minsteDato : nyStartdato;
    }

    public static String date(DateTime dateTime) {
        return DateTimeFormat.forPattern("d. MMMM yyyy").withLocale(Locale.getDefault()).print(dateTime);
    }

    public static String lagVisningUtbetalingsdato(DateTime visningsdato) {
        if (visningsdato == null) {
            return "Ingen utbetalingsdato";
        }
        return date(visningsdato);
    }
}

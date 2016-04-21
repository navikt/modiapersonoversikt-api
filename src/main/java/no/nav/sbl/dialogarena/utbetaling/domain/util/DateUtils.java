package no.nav.sbl.dialogarena.utbetaling.domain.util;

import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

public final class DateUtils {

    public static final int EKSTRA_SOKEPERIODE = 20;

    private DateUtils() {
    }

    public static final Transformer<Interval, DateTime> START = interval -> interval.getStart();

    public static final Transformer<Interval, DateTime> END = interval -> interval.getEnd();

    public static final Transformer<DateTime, LocalDate> TO_LOCAL_DATE = dateTime -> dateTime.toLocalDate();

    public static Predicate<LocalDate> isAfter(final LocalDate compare) {
        return localDate -> localDate.isAfter(compare);
    }

    public static DateTime minusDaysAndFixedAtMidnightAtDayBefore(DateTime prevDate, int minusDays) {
        return prevDate.minusDays(minusDays+1).toDateMidnight().toDateTime();
    }


    /**
     * Opprett Intervall basert på startDato og sluttDato
     * @param startDato
     * @param sluttDato
     * @return
     */
    public static Interval intervalFromStartEndDate(LocalDate startDato, LocalDate sluttDato) {
        return new Interval(startDato.toDateTimeAtStartOfDay(), sluttDato.toDateMidnight().toDateTime().plusDays(1));
    }

    protected static Predicate<DateTime> isWithinRange(final Interval intervall) {
        return dateTime -> intervall.contains(dateTime);
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
}

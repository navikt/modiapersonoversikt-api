package no.nav.sbl.dialogarena.utbetaling.domain.util;

import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

public class DateUtils {

    private DateUtils() {}

    public static final Transformer<Interval, DateTime> START = new Transformer<Interval, DateTime>() {
        @Override
        public DateTime transform(Interval interval) {
            return interval.getStart();
        }
    };

    public static final Transformer<Interval, DateTime> END = new Transformer<Interval, DateTime>() {
        @Override
        public DateTime transform(Interval interval) {
            return interval.getEnd();
        }
    };

    public static final Transformer<DateTime, LocalDate> TO_LOCAL_DATE = new Transformer<DateTime, LocalDate>() {
        @Override
        public LocalDate transform(DateTime dateTime) {
            return dateTime.toLocalDate();
        }
    };

    public static Predicate<LocalDate> isAfter(final LocalDate compare) {
        return new Predicate<LocalDate>() {
            @Override
            public boolean evaluate(LocalDate localDate) {
                return localDate.isAfter(compare);
            }
        };
    }

}

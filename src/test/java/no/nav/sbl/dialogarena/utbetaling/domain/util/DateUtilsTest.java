package no.nav.sbl.dialogarena.utbetaling.domain.util;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.junit.Test;

import static no.nav.sbl.dialogarena.utbetaling.domain.util.DateUtils.END;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.DateUtils.START;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.DateUtils.TO_LOCAL_DATE;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.DateUtils.isAfter;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class DateUtilsTest {

    @Test
    public void isAfterTest() {
        LocalDate reference = new LocalDate(2014, 1, 2);
        LocalDate future = new LocalDate(2014, 1, 3);
        LocalDate past = new LocalDate(2014, 1, 1);
        assertTrue(isAfter(reference).evaluate(future));
        assertFalse(isAfter(reference).evaluate(past));
    }

    @Test
    public void startEndTransformerTest() {
        DateTime start = DateTime.now();
        DateTime end = DateTime.now().plusHours(1);
        Interval interval = new Interval(start, end);
        assertThat(start, equalTo(START.transform(interval)));
        assertThat(end, equalTo(END.transform(interval)));
    }

    @Test
    public void toLocalDateTransformerTest() {
        DateTime dateTime = DateTime.now();
        assertThat(dateTime.toLocalDate(), equalTo(TO_LOCAL_DATE.transform(dateTime)));
    }
}

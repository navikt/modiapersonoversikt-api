package no.nav.sbl.dialogarena.utbetaling.domain.util;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.DateUtils.END;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.DateUtils.START;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.DateUtils.TO_LOCAL_DATE;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.DateUtils.isAfter;

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
        assertEquals(start, START.transform(interval));
        assertEquals(end, END.transform(interval));
    }

    @Test
    public void toLocalDateTransformerTest() {
        DateTime dateTime = DateTime.now();
        assertEquals(dateTime.toLocalDate(), TO_LOCAL_DATE.transform(dateTime));
    }
}

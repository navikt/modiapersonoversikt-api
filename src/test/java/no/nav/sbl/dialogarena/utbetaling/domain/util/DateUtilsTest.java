package no.nav.sbl.dialogarena.utbetaling.domain.util;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.junit.Test;

import static no.nav.sbl.dialogarena.utbetaling.domain.util.DateUtils.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

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

    @Test
    public void minusMonthsToMidnight() {
        DateTime dateTime = new DateTime(2015, 10, 10, 11, 11, 11);
        DateTime newDateTime = minusMonthsAndFixedAtMidnight(dateTime, 3);

        assertThat(newDateTime.getYear(), is(2015));
        assertThat(newDateTime.getMonthOfYear(), is(7));
        assertThat(newDateTime.getDayOfMonth(), is(10));
        assertThat(newDateTime.getHourOfDay(), is(0));
        assertThat(newDateTime.getMinuteOfHour(), is(0));
        assertThat(newDateTime.getSecondOfMinute(), is(0));
    }

    @Test
    public void leggerTilEkstraDagerTilStartdato() {
        LocalDate startDato = LocalDate.now();

        LocalDate nystartDato = leggTilEkstraDagerPaaStartdato(startDato);

        assertThat(nystartDato, is(startDato.minusDays(20)));
    }

    @Test
    public void leggerIkkeTilEkstraDagerTilStartdatoHvisStartdatoErHeltIStarten() {
        LocalDate minsteDato = LocalDate.now().minusYears(3).withDayOfYear(1);
        LocalDate startDato = LocalDate.now().minusYears(3).withDayOfYear(3);

        LocalDate nyStartDato = leggTilEkstraDagerPaaStartdato(startDato);

        assertThat(nyStartDato, is(minsteDato));
    }
}

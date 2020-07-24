package no.nav.kjerneinfo.common.utils;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DateUtilsTest {

    @Test
    public void testConvertDateToXmlGregorianCalendar() throws Exception {
        final DateTime date = new DateTime(2013, 2, 13, 0, 0);

        XMLGregorianCalendar xmlGregorianCalendar = DateUtils.convertDateToXmlGregorianCalendar(date.toDate());

        assertEquals(date.getYear(), xmlGregorianCalendar.getYear());
        assertEquals(date.getMonthOfYear(), xmlGregorianCalendar.getMonth());
        assertEquals(date.getDayOfMonth(), xmlGregorianCalendar.getDay());
    }

    @Test
    public void testGetRandomDate() throws Exception {
        Interval interval = new Interval(DateTime.now().minusMonths(2), DateTime.now().plusMonths(1));

        Date date = DateUtils.getRandomDate();

        assertTrue(interval.contains(date.getTime()));
    }

    @Test
    public void testGetRandomDatePair() throws Exception {
        Date[] datePair = DateUtils.getRandomDatePair();

        boolean valid = datePair[0].before(datePair[1]) || datePair[0].equals(datePair[1]);

        assertTrue(valid);
    }
}

package no.nav.modiapersonoversikt.utils;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DateUtilsTest {

    @Test
    public void testConvertDateToXmlGregorianCalendar() {
        final ZonedDateTime date = ZonedDateTime.of(2013, 2, 13, 0, 0, 0, 0, ZoneId.systemDefault());

        XMLGregorianCalendar xmlGregorianCalendar = DateUtils.convertDateToXmlGregorianCalendar(Date.from(date.toInstant()));

        assertEquals(date.getYear(), xmlGregorianCalendar.getYear());
        assertEquals(date.getMonthValue(), xmlGregorianCalendar.getMonth());
        assertEquals(date.getDayOfMonth(), xmlGregorianCalendar.getDay());
    }

    @Test
    public void testLocalDateConversionPreservesDateInSystemZone() {
        LocalDate date = LocalDate.of(2024, 1, 15);

        XMLGregorianCalendar xml = DateUtils.convertDateToXmlGregorianCalendar(DateUtils.toDate(date));

        assertEquals(date.getYear(), xml.getYear());
        assertEquals(date.getMonthValue(), xml.getMonth());
        assertEquals(date.getDayOfMonth(), xml.getDay());
    }

    @Test
    public void testOffsetDateTimeConversionPreservesInstant() {
        OffsetDateTime dateTime = OffsetDateTime.parse("2024-01-15T10:30:00+02:00");

        assertEquals(Date.from(dateTime.toInstant()), DateUtils.toDate(dateTime));
    }

    @Test
    public void testGetRandomDate() {
        ZonedDateTime start = ZonedDateTime.now().minusMonths(2);
        ZonedDateTime end = ZonedDateTime.now().plusMonths(1);

        Date date = DateUtils.getRandomDate();

        assertTrue(date.after(Date.from(start.toInstant())) && date.before(Date.from(end.toInstant())));
    }

    @Test
    public void testGetRandomDatePair() {
        Date[] datePair = DateUtils.getRandomDatePair();

        boolean valid = datePair[0].before(datePair[1]) || datePair[0].equals(datePair[1]);

        assertTrue(valid);
    }
}

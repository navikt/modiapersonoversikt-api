package no.nav.modiapersonoversikt.utils;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Utilities for working with Dates.
 *
 */
public class DateUtils {
    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);


    public static boolean datoInside(XMLGregorianCalendar datoKravMottatt, Date cutFrom, Date cutTo) {
        return toDate(datoKravMottatt).compareTo(cutFrom) >= 0 && toDate(datoKravMottatt).compareTo(cutTo) < 0;
    }

    public static Date toDate(XMLGregorianCalendar xml) {
        return xml.toGregorianCalendar().getTime();
    }

    public static Date toDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date toDate(OffsetDateTime date) {
        return Date.from(date.toInstant());
    }

    public static Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        return cal.getTime();
    }

    public static XMLGregorianCalendar convertDateToXmlGregorianCalendar(Date date) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (DatatypeConfigurationException e) {
            logger.warn("DatatypeConfigurationException", e);
            throw new RuntimeException("Klarer ikke å lage dato", e);
        }
    }

    public static XMLGregorianCalendar convertDateTimeToXmlGregorianCalendar(LocalDateTime date) {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), date.getHour(), date.getMinute(), date.getSecond(),
                        date.getNano() / 1_000_000, 0);
        } catch (DatatypeConfigurationException e) {
            logger.warn("DatatypeConfigurationException", e);
            throw new RuntimeException("Klarer ikke å lage dato", e);
        }
    }

    /**
     * @return A random date in "sort of" 3-month interval
     */
    public static Date getRandomDate() {
        ZonedDateTime end = ZonedDateTime.now().plusDays(25);
        long offset = end.minusDays(50).toInstant().toEpochMilli();
        long diff = end.toInstant().toEpochMilli() - offset;
        return new Date(offset + (long) (Math.random() * diff));
    }

    public static Date[] getRandomDatePair() {
        Date[] datepair = new Date[2];
        Date date1 = getRandomDate();
        Date date2 = getRandomDate();
        if (date2.getTime() > date1.getTime()) {
            datepair[0] = date1;
            datepair[1] = date2;
        } else {
            datepair[0] = date2;
            datepair[1] = date1;
        }
        return datepair;
    }
}

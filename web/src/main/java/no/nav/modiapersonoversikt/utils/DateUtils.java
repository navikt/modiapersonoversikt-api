package no.nav.modiapersonoversikt.utils;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
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

    public static LocalDate toLocalDate(XMLGregorianCalendar xml) {
        return LocalDate.of(
                xml.getYear(),
                xml.getMonth(),
                xml.getDay()
        );
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
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), date.getHourOfDay(), date.getMinuteOfHour(), date.getSecondOfMinute(),
                    date.getMillisOfSecond(), 0);
        } catch (DatatypeConfigurationException e) {
            logger.warn("DatatypeConfigurationException", e);
            throw new RuntimeException("Klarer ikke å lage dato", e);
        }
    }

    /**
     * @return A random date in "sort of" 3-month interval
     */
    public static Date getRandomDate() {
        DateTime end = DateTime.now().plusDays(25);
        long offset = end.minusDays(50).getMillis();
        long diff = end.getMillis() - offset;
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

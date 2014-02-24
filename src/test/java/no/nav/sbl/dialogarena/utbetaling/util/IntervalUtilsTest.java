package no.nav.sbl.dialogarena.utbetaling.util;

import no.nav.modig.lang.option.Optional;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import java.util.HashSet;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.utbetaling.util.IntervalUtils.getUncachedInterval;
import static no.nav.sbl.dialogarena.utbetaling.util.IntervalUtils.intervalContainsInclusive;
import static no.nav.sbl.dialogarena.utbetaling.util.IntervalUtils.notOverlap;
import static org.joda.time.format.DateTimeFormat.forPattern;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IntervalUtilsTest {

    @Test
    public void overlapperOver() {
        String start = "01-01-2014";
        String middle = "15-01-2014";
        String end = "01-02-2014";
        Interval filter = lagIntervall(start, end);
        Interval cache = lagIntervall(start, middle);

        Optional<Interval> intervall = notOverlap(optional(filter), cache);

        assertTrue(intervall.isSome());
        assertEquals(lagIntervall(middle, end), intervall.iterator().next());
    }

    @Test
    public void overlapperLike() {
        String start = "01-01-2014";
        String end = "01-02-2014";
        Interval filter = lagIntervall(start, end);
        Interval cache = lagIntervall(start, end);

        Optional<Interval> intervall = notOverlap(optional(filter), cache);

        assertFalse(intervall.isSome());
    }

    @Test
    public void overlapperUnder() {
        String start = "01-01-2014";
        String middle = "15-01-2014";
        String end = "01-02-2014";
        Interval filter = lagIntervall(start, end);
        Interval cache = lagIntervall(middle, end);

        Optional<Interval> intervall = notOverlap(optional(filter), cache);

        assertTrue(intervall.isSome());
        assertEquals(lagIntervall(start, middle), intervall.get());
    }

    @Test
    public void overlapperIkke() {
        String filterStart = "01-01-2014";
        String filterSlutt = "15-01-2014";
        String cacheStart = "01-02-2014";
        String cacheSlutt = "01-03-2014";
        Interval filter = lagIntervall(filterStart, filterSlutt);
        Interval cache = lagIntervall(cacheStart, cacheSlutt);

        Optional<Interval> intervall = notOverlap(optional(filter), cache);

        assertTrue(intervall.isSome());
        assertEquals(filter, intervall.get());
    }

    @Test
    public void overlapperDelvis() {
        String filterStart = "01-01-2014";
        String filterSlutt = "15-01-2014";
        String cacheStart = "10-01-2014";
        String cacheSlutt = "01-02-2014";
        Interval filter = lagIntervall(filterStart, filterSlutt);
        Interval cache = lagIntervall(cacheStart, cacheSlutt);

        Optional<Interval> intervall = notOverlap(optional(filter), cache);

        assertTrue(intervall.isSome());
        assertEquals(lagIntervall(filterStart, cacheStart), intervall.get());
    }

    @Test
    public void overlapperOverOgUnder() {
        String filterStart = "01-01-2014";
        String filterSlutt = "01-03-2014";
        String cacheStart = "01-02-2014";
        String cacheSlutt = "20-02-2014";
        Interval filter = lagIntervall(filterStart, filterSlutt);
        Interval cache = lagIntervall(cacheStart, cacheSlutt);

        Optional<Interval> intervall = notOverlap(optional(filter), cache);

        assertTrue(intervall.isSome());
        assertEquals(filter, intervall.get());
    }

    @Test
    public void overlapperInvendig() {
        String filterStart = "01-02-2014";
        String filterSlutt = "20-02-2014";
        String cacheStart = "01-01-2014";
        String cacheSlutt = "01-03-2014";
        Interval filter = lagIntervall(filterStart, filterSlutt);
        Interval cache = lagIntervall(cacheStart, cacheSlutt);

        Optional<Interval> intervall = notOverlap(optional(filter), cache);

        assertFalse(intervall.isSome());
    }

    @Test
    public void overlapperInnvendigFlereIntervaller() {
        Interval cache1 = lagIntervall("01-01-2014", "01-02-2014");
        Interval cache2 = lagIntervall("01-02-2014", "01-03-2014");
        Interval filter = lagIntervall("15-01-2014", "15-02-2014");

        Optional<Interval> optional = getUncachedInterval(filter, new HashSet<>(asList(cache1, cache2)));

        assertEquals(Optional.<Interval>none(), optional);
    }

    @Test
    public void overlapperFlereIntervaller() {
        Interval cache1 = lagIntervall("01-01-2014", "01-02-2014");
        Interval cache2 = lagIntervall("01-03-2014", "01-04-2014");
        Interval cache3 = lagIntervall("01-05-2014", "01-06-2014");
        Interval filter = lagIntervall("15-01-2014", "15-04-2014");

        Optional<Interval> optional = getUncachedInterval(filter, new HashSet<>(asList(cache1, cache2, cache3)));

        assertEquals(lagIntervall("01-02-2014", "15-04-2014"), optional.get());
    }

    @Test
    public void overlapperAlleIntervaller() {
        Interval cache1 = lagIntervall("01-02-2014", "01-03-2014");
        Interval cache2 = lagIntervall("15-02-2014", "15-03-2014");
        Interval filter = lagIntervall("01-01-2014", "01-04-2014");

        Optional<Interval> optional = getUncachedInterval(filter, new HashSet<>(asList(cache1, cache2)));

        assertEquals(filter, optional.get());
    }

    @Test
    public void containsTrueNarIntervallerErLike() {
        Interval interval = lagIntervall("01-01-2014", "01-02-2014");
        assertTrue(intervalContainsInclusive(interval, interval));
    }

    @Test
    public void containsTrueNarIntervallErInnenfor() {
        Interval interval1 = lagIntervall("01-01-2014", "01-02-2014");
        Interval interval2 = lagIntervall("10-01-2014", "20-01-2014");
        assertTrue(intervalContainsInclusive(interval1, interval2));
    }

    @Test
    public void containsFalseNarIntervalOverlapperDelvis() {
        Interval interval1 = lagIntervall("01-01-2014", "01-02-2014");
        Interval interval2 = lagIntervall("15-01-2014", "15-02-2014");
        assertFalse(intervalContainsInclusive(interval1, interval2));
    }

    @Test
    public void containsFalseNarIntervalLiggerIntillHverandre() {
        Interval interval1 = lagIntervall("01-01-2014", "01-02-2014");
        Interval interval2 = lagIntervall("01-02-2014", "01-03-2014");
        assertFalse(intervalContainsInclusive(interval1, interval2));
    }


    private static Interval lagIntervall(String start, String end) {
        DateTimeFormatter formatter = forPattern("dd-MM-yyyy");
        return new Interval(formatter.parseDateTime(start), formatter.parseDateTime(end));
    }
}

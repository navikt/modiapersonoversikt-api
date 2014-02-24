package no.nav.sbl.dialogarena.utbetaling.util;

import no.nav.modig.lang.option.Optional;
import org.joda.time.Interval;

import java.util.Set;

import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;

public class IntervalUtils {

    public static Optional<Interval> getUncachedInterval(Interval filter, Set<Interval> cachedIntervals) {
        return getUncachedInterval(optional(filter), cachedIntervals);
    }

    public static Optional<Interval> getUncachedInterval(Optional<Interval> filter, Set<Interval> remainingCached) {
        if (remainingCached.isEmpty()) {
            return filter;
        } else {
            Interval cached = remainingCached.iterator().next();
            remainingCached.remove(cached);
            return getUncachedInterval(notOverlap(filter, cached), remainingCached);
        }
    }

    public static Optional<Interval> notOverlap(Optional<Interval> optionalFilter, Interval cached) {
        if (!optionalFilter.isSome() || intervalContainsInclusive(cached, optionalFilter.get())) {
            return none();
        }
        Interval filter = optionalFilter.get();
        Interval interval = new Interval(filter);
        if (cached.contains(filter.getStart())) {
            interval = interval.withStart(cached.getEnd());
        }
        if (cached.contains(filter.getEnd().minusMillis(1))) {
            interval = interval.withEnd(cached.getStart());
        }
        return optional(interval);
    }

    public static boolean intervalContainsInclusive(Interval outer, Interval inner) {
        outer = outer.withEnd(outer.getEnd().plusMillis(1));
        return outer.contains(inner);
    }
}

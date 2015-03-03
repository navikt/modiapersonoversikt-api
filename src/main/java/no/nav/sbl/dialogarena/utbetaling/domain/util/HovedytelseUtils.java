package no.nav.sbl.dialogarena.utbetaling.domain.util;

import no.nav.modig.lang.collections.iter.ReduceFunction;
import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.YearMonth;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;

import static java.util.Collections.sort;
import static no.nav.modig.lang.collections.ComparatorUtils.compareWith;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.*;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.modig.lang.collections.TransformerUtils.first;
import static no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse.hovedytelsedato;
import static no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse.ytelsesperiode;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.DateUtils.*;
import static org.joda.time.DateTime.now;

/**
 * Hjelpefunksjoner for å jobbe med Hovedytelser.
 */
public class HovedytelseUtils {

    public static List<Record<Hovedytelse>> hentHovedytelserFraPeriode(List<Record<Hovedytelse>> hovedytelser, LocalDate startDato, LocalDate sluttDato) {
        final Interval intervall = intervalFromStartEndDate(startDato, sluttDato);
        return on(hovedytelser)
                .filter(where(hovedytelsedato, isWithinRange(intervall))).collect();
    }

    public static Map<YearMonth, List<Record<Hovedytelse>>> ytelserGroupedByYearMonth(List<Record<Hovedytelse>> hovedytelser) {
        return on(hovedytelser).map(TO_YEAR_MONTH_ENTRY).reduce(BY_YEAR_MONTH);
    }

    public static List<List<Record<Hovedytelse>>> grupperPaaHovedytelseOgPeriode(Iterable<Record<Hovedytelse>> utbetalinger) {
        List<List<Record<Hovedytelse>>> resultat = new ArrayList<>();

        Collection<List<Record<?>>> gruppertEtterHovedytelse = on(utbetalinger).reduce(indexBy(Hovedytelse.ytelse)).values();
        for (List<Record<?>> sammeHovedytelse : gruppertEtterHovedytelse) {
            sort(sammeHovedytelse, compareWith(first(ytelsesperiode).then(START)));
            resultat.addAll(on(sammeHovedytelse).reduce(splittPaaPeriode));
        }

        return resultat;
    }

    public static Predicate<Record<Hovedytelse>> betweenNowAndMonthsBefore(final int numberOfMonthsToShow) {
        return new Predicate<Record<Hovedytelse>>() {
            @Override
            public boolean evaluate(Record<Hovedytelse> hovedytelse) {
                DateTime hovedytelseDato = hovedytelse.get(hovedytelsedato);
                DateTime threshold = minusMonthsAndFixedAtMidnight(now(), numberOfMonthsToShow);
                return hovedytelseDato.isAfter(threshold);
            }
        };
    }

    protected static Interval intervalFromStartEndDate(LocalDate startDato, LocalDate sluttDato) {
        return new Interval(startDato.toDateTimeAtStartOfDay(), sluttDato.toDateMidnight().toDateTime().plusDays(1));
    }

    protected static final ReduceFunction<Entry<YearMonth, Record<Hovedytelse>>, Map<YearMonth, List<Record<Hovedytelse>>>> BY_YEAR_MONTH = new ReduceFunction<Entry<YearMonth,Record<Hovedytelse>>, Map<YearMonth, List<Record<Hovedytelse>>>>() {
        @Override
        public Map<YearMonth, List<Record<Hovedytelse>>> reduce(Map<YearMonth, List<Record<Hovedytelse>>> accumulator, Entry<YearMonth, Record<Hovedytelse>> entry) {
            if(!accumulator.containsKey(entry.getKey())) {
                accumulator.put(entry.getKey(), new ArrayList<Record<Hovedytelse>>());
            }
            accumulator.get(entry.getKey()).add(entry.getValue());
            return accumulator;
        }

        @Override
        public Map<YearMonth, List<Record<Hovedytelse>>> identity() {
            return new HashMap<>();
        }
    };

    protected static final Transformer<Record<Hovedytelse>, Entry<YearMonth, Record<Hovedytelse>>> TO_YEAR_MONTH_ENTRY = new Transformer<Record<Hovedytelse>, Entry<YearMonth, Record<Hovedytelse>>>() {
        @Override
        public Entry<YearMonth, Record<Hovedytelse>> transform(Record<Hovedytelse> ytelse) {
            int year = ytelse.get(hovedytelsedato).getYear();
            int monthOfYear = ytelse.get(hovedytelsedato).getMonthOfYear();
            YearMonth yearMonth = new YearMonth(year, monthOfYear);
            return new SimpleEntry<>(yearMonth, ytelse);
        }
    };

    private static ReduceFunction<Record<?>, List<List<Record<Hovedytelse>>>> splittPaaPeriode = new ReduceFunction<Record<?>, List<List<Record<Hovedytelse>>>>() {
        @Override
        public List<List<Record<Hovedytelse>>> reduce(List<List<Record<Hovedytelse>>> accumulator, Record<?> newValue) {
            Optional<List<Record<Hovedytelse>>> optionalMedSammePeriode = on(accumulator).filter(erISammePeriode((Record<Hovedytelse>) newValue)).head();
            List<Record<Hovedytelse>> liste;
            if (optionalMedSammePeriode.isSome()) {
                liste = optionalMedSammePeriode.get();
            } else {
                liste = new ArrayList<>();
                accumulator.add(liste);
            }
            liste.add((Record<Hovedytelse>) newValue);
            return accumulator;
        }

        @Override
        public List<List<Record<Hovedytelse>>> identity() {
            return new ArrayList<>();
        }
    };

    protected static Predicate<Collection<Record<Hovedytelse>>> erISammePeriode(final Record<Hovedytelse> hovedytelse) {
        return new Predicate<Collection<Record<Hovedytelse>>>() {
            @Override
            public boolean evaluate(Collection<Record<Hovedytelse>> utbetalinger) {
                LocalDate start = hovedytelse.get(ytelsesperiode).getStart().toLocalDate().minusDays(1);
                return !on(utbetalinger)
                        .filter(where(first(ytelsesperiode).then(END).then(TO_LOCAL_DATE),
                                either(equalTo(start)).or(isAfter(start)))).isEmpty();
            }
        };
    }

    protected static Predicate<DateTime> isWithinRange(final Interval intervall) {
        return new Predicate<DateTime>() {
            @Override
            public boolean evaluate(DateTime dateTime) {
                return intervall.contains(dateTime);
            }
        };
    }
}

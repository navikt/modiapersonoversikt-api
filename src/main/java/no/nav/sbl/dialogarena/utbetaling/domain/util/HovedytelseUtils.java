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

    /**
     * Returnerer en liste av Hovedytelser innenfor gitt periode
     * @param hovedytelser
     * @param startDato (Inclusive)
     * @param sluttDato (Exclusive)
     * @return
     */
    public static List<Record<Hovedytelse>> hovedytelserFromPeriod(List<Record<Hovedytelse>> hovedytelser, LocalDate startDato, LocalDate sluttDato) {
        final Interval intervall = intervalFromStartEndDate(startDato, sluttDato);
        return on(hovedytelser)
                .filter(where(hovedytelsedato, isWithinRange(intervall))).collect();
    }

    /**
     * Grupperer hovedytelser basert på år og måned
     * @param hovedytelser
     * @return
     */
    public static Map<YearMonth, List<Record<Hovedytelse>>> ytelserGroupedByYearMonth(List<Record<Hovedytelse>> hovedytelser) {
        return on(hovedytelser).map(TO_YEAR_MONTH_ENTRY).reduce(BY_YEAR_MONTH);
    }

    /**
     * Grupper på hovdytelser
     * Innenfor hver hovdytelse, grupper på perioder
     *
     * @param utbetalinger
     * @return
     */
    public static List<List<Record<Hovedytelse>>> groupByHovedytelseAndPeriod(List<Record<Hovedytelse>> utbetalinger) {
        Collection<List<Record<?>>> gruppertEtterHovedytelse = groupByHovedytelse(utbetalinger).values();

        List<List<Record<Hovedytelse>>> resultat = new ArrayList<>();
        for (List<Record<?>> sammeHovedytelse : gruppertEtterHovedytelse) {
            sort(sammeHovedytelse, compareWith(first(ytelsesperiode).then(START)));
            resultat.addAll(on(sammeHovedytelse).reduce(splitByPeriod));
        }

        return resultat;
    }

    /**
     * true hvis hovedytelsen er mellom now() og antall måneder tilbake i tid, gitt ved <em>numberOfMonthsToShow</em><br>
     *     F.eks<br>
     *         now: 2015-03-04 <br>
     *         numberOfMonthsToShow: 3 <br>
     *         gyldig periode: 2014-12-05 - 2015-03-04 <br>
     * @param numberOfMonthsToShow
     * @return
     */
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

    /**
     * Grupper hovedytelsene basert på ytelsen. <br>
     *     F.eks <br>
     *         Alle Dagpenger bli gruppert sammen, alle Sykepenger bli gruppert sammen osv.
     * @param ytelser
     * @return
     */
    protected static Map<String, List<Record<?>>> groupByHovedytelse(List<Record<Hovedytelse>> ytelser) {
        return on(ytelser).reduce(indexBy(Hovedytelse.ytelse));
    }

    /**
     * Opprett Intervall basert på startDato og sluttDato
     * @param startDato
     * @param sluttDato
     * @return
     */
    protected static Interval intervalFromStartEndDate(LocalDate startDato, LocalDate sluttDato) {
        return new Interval(startDato.toDateTimeAtStartOfDay(), sluttDato.toDateMidnight().toDateTime().plusDays(1));
    }

    /**
     * Slå sammen Hovedytelser som er i samme år og måned. Returner et map hvor key = YearMonth og verdien er en liste
     * over hovedytelsen innen for den gitte perioden
     */
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

    /**
     * Transformer en Hovedytelse til en Entry hvor key = YearMonth basert på <em>hovedytelsedato</em>, og verdi er Hovedytelsen.
     */
    protected static final Transformer<Record<Hovedytelse>, Entry<YearMonth, Record<Hovedytelse>>> TO_YEAR_MONTH_ENTRY = new Transformer<Record<Hovedytelse>, Entry<YearMonth, Record<Hovedytelse>>>() {
        @Override
        public Entry<YearMonth, Record<Hovedytelse>> transform(Record<Hovedytelse> ytelse) {
            int year = ytelse.get(hovedytelsedato).getYear();
            int monthOfYear = ytelse.get(hovedytelsedato).getMonthOfYear();
            YearMonth yearMonth = new YearMonth(year, monthOfYear);
            return new SimpleEntry<>(yearMonth, ytelse);
        }
    };

    /**
     * Splitt
     */
    private static ReduceFunction<Record<?>, List<List<Record<Hovedytelse>>>> splitByPeriod = new ReduceFunction<Record<?>, List<List<Record<Hovedytelse>>>>() {
        @Override
        public List<List<Record<Hovedytelse>>> reduce(List<List<Record<Hovedytelse>>> accumulator, Record<?> newValue) {
            Optional<List<Record<Hovedytelse>>> optionalMedSammePeriode = on(accumulator).filter(isWithinSamePeriod((Record<Hovedytelse>) newValue)).head();

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

    /**
     *
     * @param hovedytelse
     * @return
     */
    protected static Predicate<Collection<Record<Hovedytelse>>> isWithinSamePeriod(final Record<Hovedytelse> hovedytelse) {
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

    /**
     *
     * @param intervall
     * @return
     */
    protected static Predicate<DateTime> isWithinRange(final Interval intervall) {
        return new Predicate<DateTime>() {
            @Override
            public boolean evaluate(DateTime dateTime) {
                return intervall.contains(dateTime);
            }
        };
    }
}

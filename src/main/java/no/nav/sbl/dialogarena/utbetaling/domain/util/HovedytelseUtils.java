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

import java.util.*;

import static java.util.Collections.sort;
import static no.nav.modig.lang.collections.ComparatorUtils.compareWith;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.*;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.modig.lang.collections.TransformerUtils.first;
import static no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse.utbetalingsDato;
import static no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse.ytelse;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.DateUtils.*;

/**
 * Hjelpefunksjoner for å jobbe med Hovedytelser.
 */
public class HovedytelseUtils {

    public static Set<String> hentYtelser(List<Record<Hovedytelse>> hovedytelser) {
        return on(hovedytelser).map(ytelse).collectIn(new HashSet<String>());
    }

    public static List<Record<Hovedytelse>> hentHovedytelserFraPeriode(List<Record<Hovedytelse>> hovedytelser, LocalDate startDato, LocalDate sluttDato) {
        final Interval intervall = new Interval(startDato.toDateTimeAtStartOfDay(), sluttDato.toDateMidnight().toDateTime().plusDays(1));
        return on(hovedytelser).filter(where(Hovedytelse.utbetalingsDato, isWithinRange(intervall))).collect();
    }

    public static Map<YearMonth, List<Record<Hovedytelse>>> splittUtbetalingerPerMaaned(List<Record<Hovedytelse>> hovedytelser) {
        return on(hovedytelser).reduce(toYearMonthMap());
    }


    public static final Transformer<List<Record<Hovedytelse>>, List<List<Record<Hovedytelse>>>> groupedByHovedytelseAndPeriode = new Transformer<List<Record<Hovedytelse>>, List<List<Record<Hovedytelse>>>>() {
        @Override
        public List<List<Record<Hovedytelse>>> transform(List<Record<Hovedytelse>> hovedytelser) {
            List<List<Record<Hovedytelse>>> resultat = new ArrayList<>();

            Collection<List<Record<?>>> gruppertEtterHovedytelse = on(hovedytelser).reduce(indexBy(Hovedytelse.ytelse)).values();
            for (List<Record<?>> sammeHovedytelse : gruppertEtterHovedytelse) {
                sort(sammeHovedytelse, compareWith(first(Hovedytelse.ytelsesperiode).then(START)));
                resultat.addAll(on(sammeHovedytelse).reduce(SPLITT_PAA_PERIODE));
            }

            return resultat;
        }
    };

    private static final ReduceFunction<Record<?>, List<List<Record<Hovedytelse>>>> SPLITT_PAA_PERIODE = new ReduceFunction<Record<?>, List<List<Record<Hovedytelse>>>>() {
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

    private static Predicate<Collection<Record<Hovedytelse>>> erISammePeriode(final Record<Hovedytelse> hovedytelse) {
        return new Predicate<Collection<Record<Hovedytelse>>>() {
            @Override
            public boolean evaluate(Collection<Record<Hovedytelse>> utbetalinger) {
                LocalDate start = hovedytelse.get(Hovedytelse.ytelsesperiode).getStart().toLocalDate().minusDays(1);
                return !on(utbetalinger)
                        .filter(where(first(Hovedytelse.ytelsesperiode).then(END).then(TO_LOCAL_DATE),
                                either(equalTo(start)).or(isAfter(start)))).isEmpty();
            }
        };
    }

    private static void leggTilUtbetalingerIAarsMap(List<Record<Hovedytelse>> sorterteUtbetalinger, Map<Integer, Map<Integer, List<Record<Hovedytelse>>>> aarsMap) {
        for (Record<Hovedytelse> utbetaling : sorterteUtbetalinger) {
            int aar = utbetaling.get(utbetalingsDato).getYear();
            int maaned = utbetaling.get(utbetalingsDato).getMonthOfYear();
            leggTilNoklerForAarOgMaaned(aarsMap, aar, maaned);
            aarsMap.get(aar).get(maaned).add(utbetaling);
        }
    }

    private static void leggTilNoklerForAarOgMaaned(Map<Integer, Map<Integer, List<Record<Hovedytelse>>>> aarsMap, int aar, int maaned) {
        if (!aarsMap.containsKey(aar)) {
            aarsMap.put(aar, new LinkedHashMap<Integer, List<Record<Hovedytelse>>>());
        }
        if (!aarsMap.get(aar).containsKey(maaned)) {
            aarsMap.get(aar).put(maaned, new ArrayList<Record<Hovedytelse>>());
        }
    }

    private static List<List<Record<Hovedytelse>>> trekkUtUtbetalingerPerMaaned(Map<Integer, Map<Integer, List<Record<Hovedytelse>>>> aarsMap) {
        List<List<Record<Hovedytelse>>> utbetalingerSplittetPaaMaaned = new ArrayList<>();
        for (Map<Integer, List<Record<Hovedytelse>>> maanedsMap : aarsMap.values()) {
            for (List<Record<Hovedytelse>> utbetalingerIMaaned : maanedsMap.values()) {
                utbetalingerSplittetPaaMaaned.add(utbetalingerIMaaned);
            }
        }
        return utbetalingerSplittetPaaMaaned;
    }


    private static ReduceFunction<Record<Hovedytelse>, Map<YearMonth, List<Record<Hovedytelse>>>> toYearMonthMap() {
        return new ReduceFunction<Record<Hovedytelse>, Map<YearMonth, List<Record<Hovedytelse>>>>() {
            @Override
            public Map<YearMonth, List<Record<Hovedytelse>>> reduce(Map<YearMonth, List<Record<Hovedytelse>>> accumulator, Record<Hovedytelse> newHovedytelse) {
                int year = newHovedytelse.get(Hovedytelse.utbetalingsDato).getYear();
                int monthOfYear = newHovedytelse.get(Hovedytelse.utbetalingsDato).getMonthOfYear();
                YearMonth yearMonth = new YearMonth(year, monthOfYear);

                if(accumulator.containsKey(yearMonth)) {
                    List<Record<Hovedytelse>> tempRecord = accumulator.get(year);
                    tempRecord.add(newHovedytelse);
                    accumulator.put(yearMonth, tempRecord);
                } else {
                    ArrayList<Record<Hovedytelse>> hovedytelseListe = new ArrayList<>();
                    hovedytelseListe.add(newHovedytelse);
                    accumulator.put(yearMonth, hovedytelseListe);
                }

                return accumulator;
            }

            @Override
            public Map<YearMonth, List<Record<Hovedytelse>>> identity() {
                return new HashMap<>();
            }
        };
    }

    private static Predicate<DateTime> isWithinRange(final Interval intervall) {
        return new Predicate<DateTime>() {
            @Override
            public boolean evaluate(DateTime dateTime) {
                return intervall.contains(dateTime);
            }
        };
    }
}

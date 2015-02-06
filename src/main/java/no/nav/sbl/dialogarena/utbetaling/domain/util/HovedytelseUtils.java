package no.nav.sbl.dialogarena.utbetaling.domain.util;

import no.nav.modig.lang.collections.iter.ReduceFunction;
import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import org.apache.commons.collections15.Predicate;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import java.util.*;

import static java.util.Collections.sort;
import static no.nav.modig.lang.collections.ComparatorUtils.compareWith;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.*;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.modig.lang.collections.TransformerUtils.first;
import static no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse.ytelse;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.DateUtils.*;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.UtbetalingComparator.HOVEDYTELSE_DATO_COMPARATOR;

/**
 * Hjelpefunksjoner for å jobbe med Hovedytelser.
 */
public class HovedytelseUtils {

    public static Set<String> ytelseBeskrivelser(List<Record<Hovedytelse>> hovedytelser) {
        return on(hovedytelser).map(ytelse).collectIn(new HashSet<String>());
    }

    public static List<Record<Hovedytelse>> hentHovedytelserFraPeriode(List<Record<Hovedytelse>> hovedytelser, LocalDate startDato, LocalDate sluttDato) {
        final Interval intervall = new Interval(startDato.toDateTimeAtStartOfDay(), sluttDato.toDateMidnight().toDateTime().plusDays(1));
        return on(hovedytelser).filter(where(Hovedytelse.hovedytelsedato, isWithinRange(intervall))).collect();
    }

    public static List<List<Record<Hovedytelse>>> splittUtbetalingerPerMaaned(List<Record<Hovedytelse>> hovedytelser) {
        List<Record<Hovedytelse>> hovedytelserSortert = on(hovedytelser).collect(HOVEDYTELSE_DATO_COMPARATOR);
        Map<Integer, Map<Integer, List<Record<Hovedytelse>>>> aarsMap = new LinkedHashMap<>();
        leggTilUtbetalingerIAarsMap(hovedytelserSortert, aarsMap);
        return trekkUtUtbetalingerPerMaaned(aarsMap);
    }

    public static List<List<Record<Hovedytelse>>> grupperPaaHovedytelseOgPeriode(Iterable<Record<Hovedytelse>> utbetalinger) {
        List<List<Record<Hovedytelse>>> resultat = new ArrayList<>();

        Collection<List<Record<?>>> gruppertEtterHovedytelse = on(utbetalinger).reduce(indexBy(Hovedytelse.ytelse)).values();
        for (List<Record<?>> sammeHovedytelse : gruppertEtterHovedytelse) {
            sort(sammeHovedytelse, compareWith(first(Hovedytelse.ytelsesperiode).then(START)));
            resultat.addAll(on(sammeHovedytelse).reduce(SPLITT_PAA_PERIODE));
        }

        return resultat;
    }

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
            int aar = utbetaling.get(Hovedytelse.hovedytelsedato).getYear();
            int maaned = utbetaling.get(Hovedytelse.hovedytelsedato).getMonthOfYear();
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

    private static Predicate<DateTime> isWithinRange(final Interval intervall) {
        return new Predicate<DateTime>() {
            @Override
            public boolean evaluate(DateTime dateTime) {
                return intervall.contains(dateTime);
            }
        };
    }
}

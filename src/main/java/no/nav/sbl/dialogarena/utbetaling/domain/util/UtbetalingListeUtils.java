package no.nav.sbl.dialogarena.utbetaling.domain.util;

import no.nav.modig.lang.collections.iter.ReduceFunction;
import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.sort;
import static no.nav.modig.lang.collections.ComparatorUtils.compareWith;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.modig.lang.collections.TransformerUtils.first;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.HOVEDYTELSE;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.PERIODE;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.UtbetalingComparator.UTBETALING_DAG_YTELSE;

/**
 * Hjelpefunksjoner for å jobbe med lister av Utbetaling.
 */
public class UtbetalingListeUtils {

    public static Set<String> hentYtelser(List<Utbetaling> utbetalinger) {
        return on(utbetalinger).map(HOVEDYTELSE).collectIn(new HashSet<String>());
    }

    public static List<Utbetaling> hentUtbetalingerFraPeriode(List<Utbetaling> utbetalinger, LocalDate startDato, LocalDate sluttDato) {
        Interval intervall = new Interval(startDato.toDateTimeAtStartOfDay(), sluttDato.toDateMidnight().toDateTime().plusDays(1));
        ArrayList<Utbetaling> resultat = new ArrayList<>();
        for (Utbetaling utbetaling : utbetalinger) {
            if (intervall.contains(utbetaling.getUtbetalingsdato())) {
                resultat.add(utbetaling);
            }
        }
        return resultat;
    }

    public static List<List<Utbetaling>> splittUtbetalingerPerMaaned(List<Utbetaling> utbetalinger) {
        sort(utbetalinger, UTBETALING_DAG_YTELSE);
        Map<Integer, Map<Integer, List<Utbetaling>>> aarsMap = new LinkedHashMap<>();
        leggTilUtbetalingerIAarsMap(utbetalinger, aarsMap);
        return trekkUtUtbetalingerPerMaaned(aarsMap);
    }

    public static List<List<Utbetaling>> samleSammenLikeYtelserISammePeriode(Iterable<Utbetaling> utbetalinger) {
        List<List<Utbetaling>> resultat = new ArrayList<>();

        Collection<List<Utbetaling>> gruppertEtterHovedytelse = on(utbetalinger).reduce(indexBy(HOVEDYTELSE)).values();
        for (List<Utbetaling> sammeHovedytelse : gruppertEtterHovedytelse) {
            sort(sammeHovedytelse, compareWith(first(PERIODE).then(START)));
            resultat.addAll(on(sammeHovedytelse).reduce(SPLITT_PAA_PERIODE));
        }

        return resultat;
    }

    private static final ReduceFunction<Utbetaling, List<List<Utbetaling>>> SPLITT_PAA_PERIODE = new ReduceFunction<Utbetaling, List<List<Utbetaling>>>() {
        @Override
        public List<List<Utbetaling>> reduce(List<List<Utbetaling>> accumulator, Utbetaling newValue) {
            Optional<List<Utbetaling>> optionalMedSammePeriode = on(accumulator).filter(erISammePeriode(newValue)).head();
            List<Utbetaling> liste;
            if (optionalMedSammePeriode.isSome()) {
                liste = optionalMedSammePeriode.get();
            } else {
                liste = new ArrayList<>();
                accumulator.add(liste);
            }
            liste.add(newValue);
            return accumulator;
        }

        @Override
        public List<List<Utbetaling>> identity() {
            return new ArrayList<>();
        }
    };

    private static void leggTilUtbetalingerIAarsMap(List<Utbetaling> sorterteUtbetalinger, Map<Integer, Map<Integer, List<Utbetaling>>> aarsMap) {
        for (Utbetaling utbetaling : sorterteUtbetalinger) {
            int aar = utbetaling.getUtbetalingsdato().getYear();
            int maaned = utbetaling.getUtbetalingsdato().getMonthOfYear();
            leggTilNoklerForAarOgMaaned(aarsMap, aar, maaned);
            aarsMap.get(aar).get(maaned).add(utbetaling);
        }
    }

    private static void leggTilNoklerForAarOgMaaned(Map<Integer, Map<Integer, List<Utbetaling>>> aarsMap, int aar, int maaned) {
        if (!aarsMap.containsKey(aar)) {
            aarsMap.put(aar, new LinkedHashMap<Integer, List<Utbetaling>>());
        }
        if (!aarsMap.get(aar).containsKey(maaned)) {
            aarsMap.get(aar).put(maaned, new ArrayList<Utbetaling>());
        }
    }

    private static List<List<Utbetaling>> trekkUtUtbetalingerPerMaaned(Map<Integer, Map<Integer, List<Utbetaling>>> aarsMap) {
        List<List<Utbetaling>> utbetalingerSplittetPaaMaaned = new ArrayList<>();
        for (Map<Integer, List<Utbetaling>> maanedsMap : aarsMap.values()) {
            for (List<Utbetaling> utbetalingerIMaaned : maanedsMap.values()) {
                utbetalingerSplittetPaaMaaned.add(utbetalingerIMaaned);
            }
        }
        return utbetalingerSplittetPaaMaaned;
    }

    private static Predicate<Collection<Utbetaling>> erISammePeriode(final Utbetaling utbetaling) {
        return new Predicate<Collection<Utbetaling>>() {
            @Override
            public boolean evaluate(Collection<Utbetaling> utbetalinger) {
                return !on(utbetalinger).filter(where(first(PERIODE).then(END), isAfter(utbetaling.getPeriode().getStart().minusDays(2)))).isEmpty();
            }
        };
    }

    private static final Transformer<Interval, DateTime> START = new Transformer<Interval, DateTime>() {
        @Override
        public DateTime transform(Interval interval) {
            return interval.getStart();
        }
    };

    private static final Transformer<Interval, DateTime> END = new Transformer<Interval, DateTime>() {
        @Override
        public DateTime transform(Interval interval) {
            return interval.getEnd();
        }
    };

    private static Predicate<DateTime> isAfter(final DateTime compare) {
        return new Predicate<DateTime>() {
            @Override
            public boolean evaluate(DateTime dateTime) {
                return dateTime.isAfter(compare);
            }
        };
    }
}

package no.nav.sbl.dialogarena.utbetaling.domain.util;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.apache.commons.collections15.Transformer;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.sort;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.UtbetalingComparator.UTBETALING_DAG_YTELSE;

/**
 * Hjelpefunksjoner for å jobbe med lister av Utbetaling.
 */
public class UtbetalingListeUtils {

    private static final Transformer<Utbetaling, String> HOVEDYTELSE = new Transformer<Utbetaling, String>() {
        @Override
        public String transform(Utbetaling utbetaling) {
            return utbetaling.getHovedytelse();
        }
    };

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

}

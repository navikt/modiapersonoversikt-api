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

import static java.util.Arrays.asList;
import static java.util.Collections.sort;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.UtbetalingComparator.UTBETALING_DAG_YTELSE;

/**
 * Hjelpefunksjoner for å jobbe med lister av Utbetaling.
 */
public class UtbetalingListeUtils {

    public static Set<String> hentYtelser(Utbetaling... utbetalinger) {
        return hentYtelser(asList(utbetalinger));
    }
    public static Set<String> hentYtelser(List<Utbetaling> utbetalinger) {
        return on(utbetalinger).map(HOVEDYTELSE).collectIn(new HashSet<String>());
    }

    private static final Transformer<Utbetaling, String> HOVEDYTELSE = new Transformer<Utbetaling, String>() {
        @Override
        public String transform(Utbetaling utbetaling) {
            return utbetaling.getHovedytelse();
        }
    };

    public static List<List<Utbetaling>> splittUtbetalingerPerMaaned(List<Utbetaling> utbetalinger) {
        ArrayList<Utbetaling> omfg = new ArrayList<>(utbetalinger);
        sort(omfg, UTBETALING_DAG_YTELSE);
        Map<Integer, Map<Integer, List<Utbetaling>>> aarsMap = new LinkedHashMap<>();
        for (Utbetaling utbetaling : utbetalinger) {
            int aar = utbetaling.getUtbetalingsdato().getYear();
            int maaned = utbetaling.getUtbetalingsdato().getMonthOfYear();
            if (!aarsMap.containsKey(aar)) {
                aarsMap.put(aar, new LinkedHashMap<Integer, List<Utbetaling>>());
            }
            if (!aarsMap.get(aar).containsKey(maaned)) {
                aarsMap.get(aar).put(maaned, new ArrayList<Utbetaling>());
            }
            aarsMap.get(aar).get(maaned).add(utbetaling);
        }

        List<List<Utbetaling>> utbetalingerSplittetPaaMaaned = new ArrayList<>();
        for (Map<Integer, List<Utbetaling>> maanedsMap : aarsMap.values()) {
            for (List<Utbetaling> utbetalingerIMaaned : maanedsMap.values()) {
                sort(utbetalingerIMaaned, UTBETALING_DAG_YTELSE);
                utbetalingerSplittetPaaMaaned.add(utbetalingerIMaaned);
            }
        }
        return utbetalingerSplittetPaaMaaned;

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
}

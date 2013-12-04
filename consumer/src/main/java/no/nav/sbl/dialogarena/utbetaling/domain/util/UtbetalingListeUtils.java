package no.nav.sbl.dialogarena.utbetaling.domain.util;

import no.nav.sbl.dialogarena.utbetaling.domain.Bilag;
import no.nav.sbl.dialogarena.utbetaling.domain.Periode;
import no.nav.sbl.dialogarena.utbetaling.domain.PosteringsDetalj;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class UtbetalingListeUtils {

    public static List<List<Utbetaling>> splittUtbetalingerPerMaaned(List<Utbetaling> synligeUtbetalinger) {
        int currentMaaned = 0;
        List<Utbetaling> currentMaanedListe = new ArrayList<>();
        List<List<Utbetaling>> utbetalingerFordeltPerMaaned = new ArrayList<>();

        for (Utbetaling utbetaling : synligeUtbetalinger) {
            int maaned = utbetaling.getUtbetalingsDato().getMonthOfYear();
            if (currentMaaned == 0) {
                currentMaaned = maaned;
            }
            if (maaned != currentMaaned) {
                utbetalingerFordeltPerMaaned.add(currentMaanedListe);
                currentMaanedListe = new ArrayList<>();
                currentMaaned = maaned;
            }
            currentMaanedListe.add(utbetaling);
        }
        utbetalingerFordeltPerMaaned.add(currentMaanedListe);
        return utbetalingerFordeltPerMaaned;
    }

    public static List<Utbetaling> hentUtbetalingerFraPeriode(List<Utbetaling> utbetalinger, DateTime startDato, DateTime sluttDato) {
        Periode periode = new Periode(startDato, sluttDato);
        ArrayList<Utbetaling> resultat = new ArrayList<>();
        for (Utbetaling utbetaling : utbetalinger) {
            if (periode.containsDate(utbetaling.getUtbetalingsDato())) {
                resultat.add(utbetaling);
            }
        }
        return resultat;
    }

    public static Map<String, Double> hentYtelserOgSummerBelop(List<Utbetaling> utbetalinger) {
        Map<String, Double> ytelser = new HashMap<>();
        for (Utbetaling utbetaling : utbetalinger) {
            Map<String, Double> belopPerYtelse = utbetaling.getBelopPerYtelser();
            summerMapVerdier(ytelser, belopPerYtelse);
        }
        return ytelser;
    }

    public static Map<String, Map<String, Double>> hentYtelserOgSummerBelopPerUnderytelse(List<Utbetaling> utbetalinger) {

        List<PosteringsDetalj> detaljer = new ArrayList<>();
        for (Utbetaling utbetaling : utbetalinger) {
            for (Bilag bilag : utbetaling.getBilag()) {
                detaljer.addAll(bilag.getPosteringsDetaljer());
            }
        }
        Map<String, Map<String, Double>> ytelsesBelopMap = new HashMap<>();
        for (PosteringsDetalj detalj : detaljer) {
            leggSammenIResultatMap(ytelsesBelopMap, detalj.getHovedBeskrivelse(), detalj.getUnderBeskrivelse(), detalj.getBelop());
        }
        return ytelsesBelopMap;
    }

    public static void summerMapVerdier(Map<String, Double> resultat, Map<String, Double> doubleMap) {
        for (Map.Entry<String, Double> entry : doubleMap.entrySet()) {
            Double belop = (entry.getValue() != null ? entry.getValue() : 0.0) +
                    (resultat.get(entry.getKey()) != null ? resultat.get(entry.getKey()) : 0.0);
            resultat.put(entry.getKey(), belop);
        }
    }

    public static List<String> hentYtelserFraUtbetalinger(List<Utbetaling> utbetalinger) {
        Set<String> ytelser = hentYtelser(utbetalinger);
        ArrayList<String> list = new ArrayList<>(ytelser);
        Collections.sort(list);
        return list;
    }

    private static void leggSammenIResultatMap(Map<String, Map<String, Double>> resultatMap, String hoved, String under, Double detaljBelop) {
        Map<String, Double> map = resultatMap.get(hoved);
        if(map == null) { map = new HashMap<>();   }
        Double belop = detaljBelop + (map.get(under) != null? map.get(under) : 0.0);
        map.put(under, belop);
        resultatMap.put(hoved, map);
    }

    private static Set<String> hentYtelser(List<Utbetaling> utbetalinger) {
        Set<String> ytelser = new TreeSet<>();
        for (Utbetaling utbetaling : utbetalinger) {
            ytelser.addAll(utbetaling.getBeskrivelser());
        }
        return ytelser;
    }
}

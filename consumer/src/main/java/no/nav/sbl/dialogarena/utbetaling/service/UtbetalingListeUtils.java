package no.nav.sbl.dialogarena.utbetaling.service;

import no.nav.sbl.dialogarena.utbetaling.domain.Periode;
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

        for(Utbetaling utbetaling : synligeUtbetalinger) {
            int maaned = utbetaling.getUtbetalingsDato().getMonthOfYear();
            if(currentMaaned == 0) { currentMaaned = maaned; }
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


    public static Map<String, Double> hentYtelserOgSummerFraUtbetalinger(List<Utbetaling> utbetalinger) {

        Map<String, Double> ytelser = new HashMap<>();
        for (Utbetaling utbetaling : utbetalinger) {
            Map<String, Double> belopPerYtelse = utbetaling.getBelopPerYtelse();
            for (String key : belopPerYtelse.keySet()) {
                Double belop = belopPerYtelse.get(key) + ytelser.get(key);
                ytelser.put(key, belop);
            }
        }
        return ytelser;
    }

    public static List<String> hentYtelserFraUtbetalinger(List<Utbetaling> utbetalinger) {
        Set<String> ytelser = hentYtelser(utbetalinger);
        ArrayList<String> list = new ArrayList<>(ytelser);
        Collections.sort(list);
        return list;
    }

    private static Set<String> hentYtelser(List<Utbetaling> utbetalinger) {
        Set<String> ytelser = new TreeSet<>();
        for (Utbetaling utbetaling : utbetalinger) {
            ytelser.addAll(utbetaling.getBeskrivelser());
        }
        return ytelser;
    }


}

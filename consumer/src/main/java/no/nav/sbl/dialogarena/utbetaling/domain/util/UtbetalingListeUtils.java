package no.nav.sbl.dialogarena.utbetaling.domain.util;

import static java.util.Collections.sort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import no.nav.sbl.dialogarena.utbetaling.domain.Bilag;
import no.nav.sbl.dialogarena.utbetaling.domain.PosteringsDetalj;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;

import org.joda.time.Interval;
import org.joda.time.LocalDate;

/**
 * Hjelpefunksjoner for å jobbe med lister av Utbetaling.
 */
public class UtbetalingListeUtils {

    /**
     * Splitter en liste av Utbetalinger i en liste av utbetalinger.
     *
     * @return En liste av utbetalinger per måned
     */
    public static List<List<Utbetaling>> splittUtbetalingerPerMaaned(List<Utbetaling> utbetalinger) {
        int currentMaaned = 0;
        List<Utbetaling> currentMaanedListe = new ArrayList<>();
        List<List<Utbetaling>> utbetalingerFordeltPerMaaned = new ArrayList<>();

        for (Utbetaling utbetaling : utbetalinger) {
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

    /**
     * Filtrerer en liste av utbetalinger på periode.
     */
    public static List<Utbetaling> hentUtbetalingerFraPeriode(List<Utbetaling> utbetalinger, LocalDate startDato, LocalDate sluttDato) {
        Interval intervall = new Interval(startDato.toDateTimeAtStartOfDay(), sluttDato.toDateMidnight().toDateTime());
        ArrayList<Utbetaling> resultat = new ArrayList<>();
        for (Utbetaling utbetaling : utbetalinger) {
            if (intervall.contains(utbetaling.getUtbetalingsDato())) {
                resultat.add(utbetaling);
            }
        }
        return resultat;
    }

    /**
     * Summerer beløpene fra alle posteringsdetaljer med samme hovedbeskrivelse og underbeskrivelse.
     */
    public static Map<String, Map<String, Double>> summerBelopForUnderytelser(List<Utbetaling> utbetalinger) {

        List<PosteringsDetalj> detaljer = new ArrayList<>();
        for (Utbetaling utbetaling : utbetalinger) {
            for (Bilag bilag : utbetaling.getBilag()) {
                detaljer.addAll(bilag.getPosteringsDetaljer());
            }
        }
        return summerBelopForPosteringsDetaljer(detaljer);
    }

    private static Map<String, Map<String, Double>> summerBelopForPosteringsDetaljer(List<PosteringsDetalj> detaljer) {
        Map<String, Map<String, Double>> ytelsesBelopMap = new HashMap<>();
        for (PosteringsDetalj detalj : detaljer) {
            leggSammenIResultatMap(ytelsesBelopMap, detalj.getHovedBeskrivelse(), detalj.getUnderBeskrivelse(), detalj.getBelop());
        }
        return ytelsesBelopMap;
    }


    /**
     * Summerer doubles fra inputmap og legger dem i resultat.
     */
    public static void summerMapVerdier(Map<String, Double> resultat, Map<String, Double> input) {
        for (Map.Entry<String, Double> entry : input.entrySet()) {
            Double belop = (entry.getValue() != null ? entry.getValue() : 0.0) +
                    (resultat.get(entry.getKey()) != null ? resultat.get(entry.getKey()) : 0.0);
            resultat.put(entry.getKey(), belop);
        }
    }

    /**
     * Henter ut en sortert liste av unike hovedbeskrivelser fra en liste av utbetalinger.
     */
    public static List<String> hentYtelserFraUtbetalinger(List<Utbetaling> utbetalinger) {
        Set<String> ytelser = hentYtelser(utbetalinger);
        ArrayList<String> list = new ArrayList<>(ytelser);
        sort(list);
        return list;
    }

    /**
     * Henter ut et Set av hovedbeskrivelser fra en liste av utbetalinger.
     */
    public static Set<String> hentYtelser(List<Utbetaling> utbetalinger) {
        Set<String> ytelser = new TreeSet<>();
        for (Utbetaling utbetaling : utbetalinger) {
            ytelser.addAll(utbetaling.getBeskrivelser());
        }
        return ytelser;
    }

    /**
     * Legger til en verdi til et map av maps, med nøklene hovedKey og underKey.
     */
    private static void leggSammenIResultatMap(Map<String, Map<String, Double>> resultatMap, String hovedKey, String underKey, Double verdi) {
        Map<String, Double> map = resultatMap.get(hovedKey);
        if (map == null) {
            map = new HashMap<>();
        }
        Double belop = (verdi != null ? verdi : 0.0) + (map.get(underKey) != null ? map.get(underKey) : 0.0);
        map.put(underKey, belop);
        resultatMap.put(hovedKey, map);
    }
}

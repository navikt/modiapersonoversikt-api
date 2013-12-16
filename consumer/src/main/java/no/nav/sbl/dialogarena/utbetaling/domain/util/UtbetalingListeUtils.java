package no.nav.sbl.dialogarena.utbetaling.domain.util;

import no.nav.sbl.dialogarena.utbetaling.domain.Bilag;
import no.nav.sbl.dialogarena.utbetaling.domain.PosteringsDetalj;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.apache.commons.collections15.Transformer;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static java.util.Collections.sort;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.modig.lang.collections.ReduceUtils.sumDouble;
import static no.nav.sbl.dialogarena.utbetaling.domain.Bilag.POSTERINGSDETALJER;
import static no.nav.sbl.dialogarena.utbetaling.domain.PosteringsDetalj.HOVEDBESKRIVELSE;
import static no.nav.sbl.dialogarena.utbetaling.domain.PosteringsDetalj.UNDERBESKRIVELSE;

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
        Map<String, List<PosteringsDetalj>> perHovedYtelse =
                on(utbetalinger)
                        .flatmap(BILAG)
                        .flatmap(POSTERINGSDETALJER)
                        .reduce(indexBy(HOVEDBESKRIVELSE));

        Map<String, Map<String, Double>> resultat = new HashMap<>();
        for (String hovedytelse : perHovedYtelse.keySet()) {
            Map<String, List<PosteringsDetalj>> perUnderytelse = on(perHovedYtelse.get(hovedytelse)).reduce(indexBy(UNDERBESKRIVELSE));
            Map<String, Double> belopPerUnderytelse = new HashMap<>();
            for (String underytelse : perUnderytelse.keySet()) {
                Double sumBelop = on(perUnderytelse.get(underytelse)).map(PosteringsDetalj.BELOP).reduce(sumDouble);
                belopPerUnderytelse.put(underytelse, sumBelop);
            }
            resultat.put(hovedytelse, belopPerUnderytelse);
        }
        return resultat;
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

    private static final Transformer<Utbetaling, List<Bilag>> BILAG = new Transformer<Utbetaling, List<Bilag>>() {
        @Override
        public List<Bilag> transform(Utbetaling utbetaling) {
            return utbetaling.bilag;
        }
    };

}

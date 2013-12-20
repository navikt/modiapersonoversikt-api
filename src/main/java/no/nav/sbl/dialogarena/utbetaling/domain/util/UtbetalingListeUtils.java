package no.nav.sbl.dialogarena.utbetaling.domain.util;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.apache.commons.collections15.Transformer;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;

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
            int maaned = utbetaling.getUtbetalingsdato().getMonthOfYear();
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

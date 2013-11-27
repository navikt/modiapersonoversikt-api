package no.nav.sbl.dialogarena.utbetaling.service;


import no.nav.sbl.dialogarena.utbetaling.domain.Periode;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public final class UtbetalingsDatakilde {

    private static UtbetalingsDatakilde instance;

    private List<Utbetaling> utbetalinger;

    private UtbetalingsDatakilde() {
    }

    public static UtbetalingsDatakilde getKilde() {
        if(instance == null) {
            instance = new UtbetalingsDatakilde();
        }
        return instance;
    }

    public List<Utbetaling> getUtbetalinger() {
        return utbetalinger;
    }

    public void refreshUtbetalinger(String fnr, DateTime startdato, DateTime sluttdato, UtbetalingService service) {
        utbetalinger = service.hentUtbetalinger(fnr, startdato, sluttdato);
    }

    public static List<Utbetaling> finnUtbetalingerIPeriode(List<Utbetaling> utbetalinger, Periode periode) {
        List<Utbetaling> resultat = new ArrayList<>();
        for (Utbetaling utbetaling : utbetalinger) {
            if(periode.containsDate(utbetaling.getUtbetalingsDato())) {
                resultat.add(utbetaling);
            }
        }
        return resultat;
    }
}

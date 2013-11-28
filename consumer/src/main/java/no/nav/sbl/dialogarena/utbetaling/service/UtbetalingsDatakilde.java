package no.nav.sbl.dialogarena.utbetaling.service;


import no.nav.sbl.dialogarena.utbetaling.domain.FilterParameters;
import no.nav.sbl.dialogarena.utbetaling.domain.Periode;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.logikk.Filtrerer;
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

    public List<Utbetaling> hentUtbetalinger(DateTime startDato, DateTime sluttDato) {
        return finnUtbetalingerIPeriode(utbetalinger, new Periode(startDato, sluttDato));
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

    public List<Utbetaling> getSynligeUtbetalinger(FilterParameters params) {
        return getSynligeUtbetalinger(utbetalinger, params);
    }

    public static List<Utbetaling> getSynligeUtbetalinger(List<Utbetaling> alleUtbetalinger, FilterParameters params) {
        List<Utbetaling> synligeUtbetalinger = new ArrayList<>();
        for (Utbetaling utbetaling : alleUtbetalinger) {
            boolean erSynlig = utbetalingErSynlig(utbetaling, params);
            if(erSynlig) {
                synligeUtbetalinger.add(utbetaling);
            }
        }
        return synligeUtbetalinger;
    }

    private static boolean utbetalingErSynlig(Utbetaling utbetaling, FilterParameters params) {
        boolean innenforDatoer = Filtrerer.filtrerPaaDatoer(utbetaling.getUtbetalingsDato().toLocalDate(), params.startDato, params.sluttDato);
        boolean brukerSkalVises = Filtrerer.filtrerPaaMottaker(utbetaling.getMottaker().getMottakertypeType(), params.visArbeidsgiver, params.visBruker);
        return innenforDatoer && brukerSkalVises;
    }
}

package no.nav.sbl.dialogarena.utbetaling.service;


import no.nav.sbl.dialogarena.utbetaling.domain.Periode;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.logikk.Filtrerer;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

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

    public List<Utbetaling> getSynligeUtbetalinger(LocalDate start, LocalDate slutt, boolean visArbeidsgiver, boolean visBruker) {
        return getSynligeUtbetalinger(utbetalinger, start, slutt, visArbeidsgiver, visBruker);
    }

    public static List<Utbetaling> getSynligeUtbetalinger(List<Utbetaling> alleUtbetalinger, LocalDate start, LocalDate slutt, boolean visArbeidsgiver, boolean visBruker) {
        List<Utbetaling> synligeUtbetalinger = new ArrayList<>();
        for (Utbetaling utbetaling : alleUtbetalinger) {
            boolean erSynlig = utbetalingErSynlig(utbetaling, start, slutt, visArbeidsgiver, visBruker);
            if(erSynlig) {
                synligeUtbetalinger.add(utbetaling);
            }
        }
        return synligeUtbetalinger;
    }

    private static boolean utbetalingErSynlig(Utbetaling utbetaling, LocalDate start, LocalDate slutt, boolean visArbeidsgiver, boolean visBruker) {
        boolean innenforDatoer = Filtrerer.filtrerPaaDatoer(utbetaling.getUtbetalingsDato().toLocalDate(), start, slutt);
        boolean brukerSkalVises = Filtrerer.filtrerPaaMottaker(utbetaling.getMottaker().getMottakertypeType(), visArbeidsgiver, visBruker);
        return innenforDatoer && brukerSkalVises;
    }
}

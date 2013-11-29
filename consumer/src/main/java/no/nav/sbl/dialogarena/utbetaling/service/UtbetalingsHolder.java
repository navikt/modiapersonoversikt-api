package no.nav.sbl.dialogarena.utbetaling.service;


import no.nav.sbl.dialogarena.utbetaling.domain.FilterParametere;
import no.nav.sbl.dialogarena.utbetaling.domain.Periode;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.logikk.Filtrerer;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.DEFAULT_SLUTTDATO;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.DEFAULT_STARTDATO;

public final class UtbetalingsHolder implements Serializable {

    private UtbetalingService utbetalingService;
    private List<Utbetaling> utbetalinger;

    public UtbetalingsHolder(String fnr, UtbetalingService utbetalingService) {
        this.utbetalingService = utbetalingService;
        refreshUtbetalinger(fnr, DEFAULT_STARTDATO.toDateTimeAtStartOfDay(), DEFAULT_SLUTTDATO.toDateTimeAtStartOfDay());
    }

    public List<Utbetaling> hentUtbetalinger(DateTime startDato, DateTime sluttDato) {
        Periode periode = new Periode(startDato, sluttDato);
        List<Utbetaling> resultat = new ArrayList<>();
        for (Utbetaling utbetaling : utbetalinger) {
            if (periode.containsDate(utbetaling.getUtbetalingsDato())) {
                resultat.add(utbetaling);
            }
        }
        return resultat;
    }

    public List<Utbetaling> getSynligeUtbetalinger(FilterParametere params) {
        List<Utbetaling> synligeUtbetalinger = new ArrayList<>();
        for (Utbetaling utbetaling : utbetalinger) {
            boolean erSynlig = utbetalingErSynlig(utbetaling, params);
            if (erSynlig) {
                synligeUtbetalinger.add(utbetaling);
            }
        }
        return synligeUtbetalinger;
    }

    private static boolean utbetalingErSynlig(Utbetaling utbetaling, FilterParametere params) {
        boolean innenforDatoer = Filtrerer.filtrerPaaDatoer(utbetaling.getUtbetalingsDato().toLocalDate(), params.startDato, params.sluttDato);
        boolean brukerSkalVises = Filtrerer.filtrerPaaMottaker(utbetaling.getMottaker().getMottakertypeType(), params.visArbeidsgiver, params.visBruker);
        return innenforDatoer && brukerSkalVises;
    }

    public List<Utbetaling> getUtbetalinger() {
        return utbetalinger;
    }

    public void refreshUtbetalinger(String fnr, DateTime startdato, DateTime sluttdato) {
        utbetalinger = utbetalingService.hentUtbetalinger(fnr, startdato, sluttdato);
    }

}

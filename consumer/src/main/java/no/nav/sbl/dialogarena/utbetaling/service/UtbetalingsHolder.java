package no.nav.sbl.dialogarena.utbetaling.service;


import no.nav.sbl.dialogarena.utbetaling.domain.FilterParametere;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.DEFAULT_SLUTTDATO;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.DEFAULT_STARTDATO;
import static no.nav.sbl.dialogarena.utbetaling.logikk.Filter.filtrer;
import static no.nav.sbl.dialogarena.utbetaling.service.UtbetalingListeUtils.hentUtbetalingerFraPeriode;
import static no.nav.sbl.dialogarena.utbetaling.service.UtbetalingListeUtils.splittUtbetalingerPerMaaned;

public final class UtbetalingsHolder implements Serializable {

    private UtbetalingService utbetalingService;
    private List<Utbetaling> utbetalinger;

    public UtbetalingsHolder(String fnr, UtbetalingService utbetalingService) {
        this.utbetalingService = utbetalingService;
        refreshUtbetalinger(fnr, DEFAULT_STARTDATO.toDateTimeAtStartOfDay(), DEFAULT_SLUTTDATO.toDateTimeAtStartOfDay());
    }

    public void refreshUtbetalinger(String fnr, DateTime startdato, DateTime sluttdato) {
        utbetalinger = utbetalingService.hentUtbetalinger(fnr, startdato, sluttdato);
    }

    public List<List<Utbetaling>> hentFiltrertUtbetalingerPerMaaned(FilterParametere filterParametre) {
        return splittUtbetalingerPerMaaned(getSynligeUtbetalinger(filterParametre));
    }

    public List<Utbetaling> hentUtbetalinger(DateTime startDato, DateTime sluttDato) {
        return hentUtbetalingerFraPeriode(utbetalinger, startDato, sluttDato);
    }

    public List<Utbetaling> getSynligeUtbetalinger(FilterParametere params) {
        List<Utbetaling> synligeUtbetalinger = new ArrayList<>();
        for (Utbetaling utbetaling : utbetalinger) {
            if (filtrer(utbetaling, params)) {
                synligeUtbetalinger.add(utbetaling);
            }
        }
        return synligeUtbetalinger;
    }
}

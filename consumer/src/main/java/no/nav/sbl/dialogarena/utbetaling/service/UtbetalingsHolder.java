package no.nav.sbl.dialogarena.utbetaling.service;


import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.filter.FilterParametere;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.DEFAULT_SLUTTDATO;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.DEFAULT_STARTDATO;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils.hentUtbetalingerFraPeriode;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils.splittUtbetalingerPerMaaned;
import static no.nav.sbl.dialogarena.utbetaling.filter.Filter.filtrer;

public class UtbetalingsHolder implements Serializable {

    @Inject
    private UtbetalingService utbetalingService;

    private List<Utbetaling> utbetalinger;
    private UtbetalingServiceResultat resultat;

    public UtbetalingServiceResultat withFnr(String fnr) {
        if (resultat == null || !resultat.fnr.equals(fnr)) {
            resultat = new UtbetalingServiceResultat(fnr);
        }
        return resultat;
    }

    public UtbetalingServiceResultat getResultat() {
        return resultat;
    }

    public final class UtbetalingServiceResultat {

        private String fnr;

        private UtbetalingServiceResultat(String fnr) {
            this.fnr = fnr;
            refreshUtbetalinger(DEFAULT_STARTDATO.toDateTimeAtStartOfDay(), DEFAULT_SLUTTDATO.toDateTimeAtStartOfDay());
        }

        public void refreshUtbetalinger(DateTime startdato, DateTime sluttdato) {
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

}

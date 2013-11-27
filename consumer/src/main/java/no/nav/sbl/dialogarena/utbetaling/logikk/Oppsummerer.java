package no.nav.sbl.dialogarena.utbetaling.logikk;


import no.nav.sbl.dialogarena.utbetaling.domain.Oppsummering;
import no.nav.sbl.dialogarena.utbetaling.domain.Periode;

import static no.nav.sbl.dialogarena.utbetaling.logikk.OppsummeringsKalkulator.regnUtOppsummering;
import static no.nav.sbl.dialogarena.utbetaling.service.UtbetalingsDatakilde.finnUtbetalingerIPeriode;
import static no.nav.sbl.dialogarena.utbetaling.service.UtbetalingsDatakilde.getKilde;


public class Oppsummerer {

    public Oppsummering lagOppsummering(Periode periode) {
        return regnUtOppsummering(finnUtbetalingerIPeriode(getKilde().getUtbetalinger(), periode));
    }
}

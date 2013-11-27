package no.nav.sbl.dialogarena.utbetaling.logikk;


import no.nav.sbl.dialogarena.utbetaling.domain.Oppsummering;
import no.nav.sbl.dialogarena.utbetaling.domain.Periode;

import static no.nav.sbl.dialogarena.utbetaling.logikk.OppsummeringsKalkulator.finnUtbetalingerIPeriode;
import static no.nav.sbl.dialogarena.utbetaling.logikk.OppsummeringsKalkulator.regnUtOppsummering;
import static no.nav.sbl.dialogarena.utbetaling.service.UtbetalingsDatakilde.get;

public class LagOppsummering {

    public Oppsummering lagOppsummering(Periode periode) {
        return regnUtOppsummering(finnUtbetalingerIPeriode(get().getUtbetalinger(), periode));
    }
}

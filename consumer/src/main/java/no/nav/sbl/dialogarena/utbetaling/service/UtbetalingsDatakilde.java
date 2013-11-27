package no.nav.sbl.dialogarena.utbetaling.service;


import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.List;

public final class UtbetalingsDatakilde {

    private static UtbetalingsDatakilde instance;

    @Inject
    private UtbetalingService utbetalingService;
    private List<Utbetaling> utbetalinger;

    private UtbetalingsDatakilde() { }

    public static UtbetalingsDatakilde get() {
        if(instance == null) {
            instance = new UtbetalingsDatakilde();
        }
        return instance;
    }

    public List<Utbetaling> getUtbetalinger() {
        return utbetalinger;
    }

    public void refreshUtbetalinger(String fnr, DateTime startdato, DateTime sluttdato) {
        utbetalinger = utbetalingService.hentUtbetalinger(fnr, startdato, sluttdato);
    }
}

package no.nav.sbl.dialogarena.utbetaling.service;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.domain.UtbetalingBuilder;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class UtbetalingService {

    public List<Utbetaling> hentUtbetalinger(String fnr){
        List<Utbetaling> utbetalinger = new ArrayList<>();
        Utbetaling utbetaling = new UtbetalingBuilder().createUtbetaling();
        Utbetaling utbetaling2 = new UtbetalingBuilder().setPeriode("2010.01.23-2011.01.24").setNettoBelop(1000.0).setStatuskode("12").setBeskrivelse("Uføre").setUtbetalingsDato(new DateTime().now().minusDays(4)).createUtbetaling();
        utbetalinger.add(utbetaling);
        utbetalinger.add(utbetaling2);
        return utbetalinger;
    }

}

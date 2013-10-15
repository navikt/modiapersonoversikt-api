package no.nav.sbl.dialogarena.utbetaling.service;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.domain.UtbetalingBuilder;

import java.util.ArrayList;
import java.util.List;

public class UtbetalingService {

    public List<Utbetaling> hentUtbetalinger(String fnr){
        List<Utbetaling> utbetalinger = new ArrayList<>();
        Utbetaling utbetaling = new UtbetalingBuilder().createUtbetaling();
        utbetalinger.add(utbetaling);
        return utbetalinger;
    }

}

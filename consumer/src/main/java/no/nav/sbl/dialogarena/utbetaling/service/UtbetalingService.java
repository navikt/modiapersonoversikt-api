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
        Utbetaling utbetaling3 = new UtbetalingBuilder().setPeriode("2010.02.23-2011.02.24").setNettoBelop(2000.0).setStatuskode("12").setBeskrivelse("Trygd").setUtbetalingsDato(new DateTime().now().minusDays(7)).createUtbetaling();
        Utbetaling utbetaling4 = new UtbetalingBuilder().setPeriode("2010.03.23-2011.03.24").setNettoBelop(3000.10).setStatuskode("12").setBeskrivelse("Barnepenger").setUtbetalingsDato(new DateTime().now().minusDays(10)).createUtbetaling();
        Utbetaling utbetaling5 = new UtbetalingBuilder().setPeriode("2010.04.23-2011.04.24").setNettoBelop(4000.0).setStatuskode("12").setBeskrivelse("Trygd").setUtbetalingsDato(new DateTime().now().minusDays(40)).createUtbetaling();
        Utbetaling utbetaling6 = new UtbetalingBuilder().setPeriode("2010.05.23-2011.05.24").setNettoBelop(5100.50).setStatuskode("12").setBeskrivelse("APGrunnbeløp").setUtbetalingsDato(new DateTime().now().minusDays(84)).createUtbetaling();
        Utbetaling utbetaling7 = new UtbetalingBuilder().setPeriode("2010.06.23-2011.06.24").setNettoBelop(6000.0).setStatuskode("12").setBeskrivelse("Pensjon").setUtbetalingsDato(new DateTime().now().minusDays(200)).createUtbetaling();
        utbetalinger.add(utbetaling);
        utbetalinger.add(utbetaling2);
        utbetalinger.add(utbetaling3);
        utbetalinger.add(utbetaling4);
        utbetalinger.add(utbetaling5);
        utbetalinger.add(utbetaling6);
        utbetalinger.add(utbetaling7);
        return utbetalinger;
    }

}

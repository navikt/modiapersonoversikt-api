package no.nav.sbl.dialogarena.utbetaling.logikk;


import no.nav.sbl.dialogarena.utbetaling.domain.Oppsummering;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.domain.UtbetalingBuilder;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OppsummeringsKalkulatorTest {

    @Test
    public void testRegnUtOppsummering() throws Exception {
        Utbetaling utbetaling1 = new UtbetalingBuilder().setNettoBelop(1000.0).setBruttoBelop(1300.0).createUtbetaling();
        Utbetaling utbetaling2 = new UtbetalingBuilder().setNettoBelop(500.0).setBruttoBelop(900.0).createUtbetaling();

        List<Utbetaling> utbetalinger = Arrays.asList(utbetaling1, utbetaling2);
        Oppsummering oppsummering = OppsummeringsKalkulator.regnUtOppsummering(utbetalinger);

        assertThat(oppsummering.brutto, is(2200.0));
        assertThat(oppsummering.utbetalt, is(1500.0));
        assertThat(oppsummering.trekk, is(700.0));
    }
}

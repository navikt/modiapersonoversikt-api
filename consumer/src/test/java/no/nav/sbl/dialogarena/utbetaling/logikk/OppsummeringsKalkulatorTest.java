package no.nav.sbl.dialogarena.utbetaling.logikk;


import no.nav.sbl.dialogarena.utbetaling.domain.Bilag;
import no.nav.sbl.dialogarena.utbetaling.domain.BilagBuilder;
import no.nav.sbl.dialogarena.utbetaling.domain.Oppsummering;
import no.nav.sbl.dialogarena.utbetaling.domain.PosteringsDetalj;
import no.nav.sbl.dialogarena.utbetaling.domain.PosteringsDetaljBuilder;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.domain.UtbetalingBuilder;
import no.nav.sbl.dialogarena.utbetaling.domain.util.OppsummeringsKalkulator;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OppsummeringsKalkulatorTest {

    @Test
    public void testRegnUtOppsummering_Summer() throws Exception {
        Utbetaling utbetaling1 = new UtbetalingBuilder().setTrekk(300.0).setNettoBelop(1000.0).setBruttoBelop(1300.0).createUtbetaling();
        Utbetaling utbetaling2 = new UtbetalingBuilder().setTrekk(400.0).setNettoBelop(500.0).setBruttoBelop(900.0).createUtbetaling();

        List<Utbetaling> utbetalinger = asList(utbetaling1, utbetaling2);
        Oppsummering oppsummering = OppsummeringsKalkulator.regnUtOppsummering(utbetalinger);

        assertThat(oppsummering.brutto, is(2200.0));
        assertThat(oppsummering.utbetalt, is(1500.0));
        assertThat(oppsummering.trekk, is(700.0));
    }

    @Test
    public void testRegnUtOppsummering_Ytelser() throws Exception {
        PosteringsDetalj detalj = new PosteringsDetaljBuilder().setHovedBeskrivelse("Alderspensjon").setBelop(1000.0).createPosteringsDetalj();
        Bilag bilag1 = new BilagBuilder().setPosteringsDetaljer(asList(detalj)).createBilag();
        Bilag bilag2 = new BilagBuilder().setPosteringsDetaljer(asList(detalj, detalj)).createBilag();
        Bilag bilag3 = new BilagBuilder().setPosteringsDetaljer(asList(detalj, detalj)).createBilag();

        Utbetaling utbetaling = new UtbetalingBuilder().setBilag(asList(bilag1, bilag2, bilag3)).createUtbetaling();
        Oppsummering oppsummering = OppsummeringsKalkulator.regnUtOppsummering(asList(utbetaling));

        assertThat(oppsummering.ytelserUtbetalt.get("Alderspensjon").get("-"), is(5000.0));
    }

}

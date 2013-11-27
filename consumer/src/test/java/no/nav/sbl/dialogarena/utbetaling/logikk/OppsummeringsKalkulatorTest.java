package no.nav.sbl.dialogarena.utbetaling.logikk;


import no.nav.sbl.dialogarena.utbetaling.domain.Oppsummering;
import no.nav.sbl.dialogarena.utbetaling.domain.Periode;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.domain.UtbetalingBuilder;
import org.joda.time.DateTime;
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

    @Test
    public void finnUtbetalingerIPeriode_AlleUtbetalingerInnenfor() throws Exception {
        DateTime dato1 = DateTime.now().minusDays(10);
        DateTime dato2 = DateTime.now().minusDays(5);
        Periode periode = new Periode(DateTime.now().minusDays(15), DateTime.now().minusDays(1));

        Utbetaling utbetaling1 = new UtbetalingBuilder().setUtbetalingsDato(dato1).createUtbetaling();
        Utbetaling utbetaling2 = new UtbetalingBuilder().setUtbetalingsDato(dato2).createUtbetaling();

        List<Utbetaling> utbetalinger = Arrays.asList(utbetaling1, utbetaling2);
        List<Utbetaling> utbetalingsResultat = OppsummeringsKalkulator.finnUtbetalingerIPeriode(utbetalinger, periode);

        assertThat(utbetalingsResultat.size(), is(2));
        assertThat(utbetalingsResultat.get(0).getBeskrivelse(), is(utbetaling1.getBeskrivelse()));
        assertThat(utbetalingsResultat.get(0).getUtbetalingId(), is(utbetaling1.getUtbetalingId()));
        assertThat(utbetalingsResultat.get(1).getBeskrivelse(), is(utbetaling2.getBeskrivelse()));
        assertThat(utbetalingsResultat.get(1).getUtbetalingId(), is(utbetaling2.getUtbetalingId()));
    }


    @Test
    public void finnUtbetalingerIPeriode_EnUtbetalingInnenfor() throws Exception {
        DateTime dato1 = DateTime.now().minusDays(10);
        DateTime dato2 = DateTime.now().minusDays(0);
        Periode periode = new Periode(DateTime.now().minusDays(15), DateTime.now().minusDays(1));

        Utbetaling utbetaling1 = new UtbetalingBuilder().setUtbetalingsDato(dato1).createUtbetaling();
        Utbetaling utbetaling2 = new UtbetalingBuilder().setUtbetalingsDato(dato2).createUtbetaling();

        List<Utbetaling> utbetalinger = Arrays.asList(utbetaling1, utbetaling2);
        List<Utbetaling> utbetalingsResultat = OppsummeringsKalkulator.finnUtbetalingerIPeriode(utbetalinger, periode);

        assertThat(utbetalingsResultat.size(), is(1));
        assertThat(utbetalingsResultat.get(0).getBeskrivelse(), is(utbetaling1.getBeskrivelse()));
        assertThat(utbetalingsResultat.get(0).getUtbetalingId(), is(utbetaling1.getUtbetalingId()));
    }

    @Test
    public void finnUtbetalingerIPeriode_IngenUtbetalingerInnenfor() throws Exception {
        DateTime dato1 = DateTime.now().minusDays(16);
        DateTime dato2 = DateTime.now().minusDays(0);
        Periode periode = new Periode(DateTime.now().minusDays(15), DateTime.now().minusDays(1));

        Utbetaling utbetaling1 = new UtbetalingBuilder().setUtbetalingsDato(dato1).createUtbetaling();
        Utbetaling utbetaling2 = new UtbetalingBuilder().setUtbetalingsDato(dato2).createUtbetaling();

        List<Utbetaling> utbetalinger = Arrays.asList(utbetaling1, utbetaling2);
        List<Utbetaling> utbetalingsResultat = OppsummeringsKalkulator.finnUtbetalingerIPeriode(utbetalinger, periode);

        assertThat(utbetalingsResultat.size(), is(0));
    }


}

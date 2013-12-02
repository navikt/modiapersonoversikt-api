package no.nav.sbl.dialogarena.utbetaling.service;


import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.domain.UtbetalingBuilder;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.utbetaling.service.UtbetalingListeUtils.*;
import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UtbetalingsHolderTest {

    private static final String FNR = "12121299999";
    private UtbetalingService utbetalingService = mock(UtbetalingService.class);
    private UtbetalingsHolder utbetalingsHolder = new UtbetalingsHolder(FNR, utbetalingService);

    @Test
    public void finnUtbetalingerIPeriode_AlleUtbetalingerInnenfor() throws Exception {
        DateTime dato1 = now().minusDays(10);
        DateTime dato2 = now().minusDays(5);
        DateTime periodeStart = DateTime.now().minusDays(15);
        DateTime periodeSlutt = DateTime.now().minusDays(1);

        Utbetaling utbetaling1 = new UtbetalingBuilder().setUtbetalingsDato(dato1).createUtbetaling();
        Utbetaling utbetaling2 = new UtbetalingBuilder().setUtbetalingsDato(dato2).createUtbetaling();

        List<Utbetaling> utbetalinger = asList(utbetaling1, utbetaling2);
        when(utbetalingService.hentUtbetalinger(FNR, dato1, dato2)).thenReturn(utbetalinger);
        utbetalingsHolder.refreshUtbetalinger(FNR, dato1, dato2);
        List<Utbetaling> utbetalingsResultat = utbetalingsHolder.hentUtbetalinger(periodeStart, periodeSlutt);

        assertThat(utbetalingsResultat.size(), is(2));
        assertThat(utbetalingsResultat.get(0).getBeskrivelse(), is(utbetaling1.getBeskrivelse()));
        assertThat(utbetalingsResultat.get(0).getUtbetalingId(), is(utbetaling1.getUtbetalingId()));
        assertThat(utbetalingsResultat.get(1).getBeskrivelse(), is(utbetaling2.getBeskrivelse()));
        assertThat(utbetalingsResultat.get(1).getUtbetalingId(), is(utbetaling2.getUtbetalingId()));
    }

    @Test
    public void finnUtbetalingerIPeriode_EnUtbetalingInnenfor() throws Exception {
        DateTime dato1 = now().minusDays(10);
        DateTime dato2 = now();
        DateTime periodeStart = DateTime.now().minusDays(15);
        DateTime periodeSlutt = DateTime.now().minusDays(1);

        Utbetaling utbetaling1 = new UtbetalingBuilder().setUtbetalingsDato(dato1).createUtbetaling();
        Utbetaling utbetaling2 = new UtbetalingBuilder().setUtbetalingsDato(dato2).createUtbetaling();

        List<Utbetaling> utbetalinger = asList(utbetaling1, utbetaling2);
        when(utbetalingService.hentUtbetalinger(FNR, dato1, dato2)).thenReturn(utbetalinger);
        utbetalingsHolder.refreshUtbetalinger(FNR, dato1, dato2);
        List<Utbetaling> utbetalingsResultat = utbetalingsHolder.hentUtbetalinger(periodeStart, periodeSlutt);

        assertThat(utbetalingsResultat.size(), is(1));
        assertThat(utbetalingsResultat.get(0).getBeskrivelse(), is(utbetaling1.getBeskrivelse()));
        assertThat(utbetalingsResultat.get(0).getUtbetalingId(), is(utbetaling1.getUtbetalingId()));
    }

    @Test
    public void finnUtbetalingerIPeriode_IngenUtbetalingerInnenfor() throws Exception {
        DateTime dato1 = now().minusDays(16);
        DateTime dato2 = now();
        DateTime periodeStart = DateTime.now().minusDays(15);
        DateTime periodeSlutt = DateTime.now().minusDays(1);

        Utbetaling utbetaling1 = new UtbetalingBuilder().setUtbetalingsDato(dato1).createUtbetaling();
        Utbetaling utbetaling2 = new UtbetalingBuilder().setUtbetalingsDato(dato2).createUtbetaling();

        List<Utbetaling> utbetalinger = asList(utbetaling1, utbetaling2);
        when(utbetalingService.hentUtbetalinger(FNR, dato1, dato2)).thenReturn(utbetalinger);
        utbetalingsHolder.refreshUtbetalinger(FNR, dato1, dato2);
        List<Utbetaling> utbetalingsResultat = utbetalingsHolder.hentUtbetalinger(periodeStart, periodeSlutt);

        assertThat(utbetalingsResultat.size(), is(0));
    }


    @Test
    public void lagListeAvUtbetalingsLister_SammenhengendeMaaneder() throws Exception {
        DateTime februarDato = DateTime.now().withMonthOfYear(1);
        DateTime marsDato = DateTime.now().withMonthOfYear(2);
        DateTime aprilDato = DateTime.now().withMonthOfYear(3);

        Utbetaling utbetaling1 = new UtbetalingBuilder().setUtbetalingsDato(februarDato).createUtbetaling();
        Utbetaling utbetaling2 = new UtbetalingBuilder().setUtbetalingsDato(februarDato).createUtbetaling();
        Utbetaling utbetaling3 = new UtbetalingBuilder().setUtbetalingsDato(marsDato).createUtbetaling();
        Utbetaling utbetaling4 = new UtbetalingBuilder().setUtbetalingsDato(aprilDato).createUtbetaling();
        List<Utbetaling> utbetalinger = asList(utbetaling1, utbetaling2, utbetaling3, utbetaling4);

        List<List<Utbetaling>> lists = splittUtbetalingerPerMaaned(utbetalinger);

        assertThat(lists.size(), is(3));
        assertThat(lists.get(0).size(), is(2));
        assertThat(lists.get(1).size(), is(1));
        assertThat(lists.get(2).size(), is(1));
    }

    @Test
    public void lagListeAvUtbetalingsLister_IkkeSammenhengendeMaaneder() throws Exception {
        DateTime februarDato = DateTime.now().withMonthOfYear(1);
        DateTime juniDato = DateTime.now().withMonthOfYear(5);
        DateTime augustDato = DateTime.now().withMonthOfYear(7);

        Utbetaling utbetaling1 = new UtbetalingBuilder().setUtbetalingsDato(februarDato).createUtbetaling();
        Utbetaling utbetaling2 = new UtbetalingBuilder().setUtbetalingsDato(februarDato).createUtbetaling();
        Utbetaling utbetaling3 = new UtbetalingBuilder().setUtbetalingsDato(juniDato).createUtbetaling();
        Utbetaling utbetaling4 = new UtbetalingBuilder().setUtbetalingsDato(augustDato).createUtbetaling();
        List<Utbetaling> utbetalinger = asList(utbetaling1, utbetaling2, utbetaling3, utbetaling4);

        List<List<Utbetaling>> lists = splittUtbetalingerPerMaaned(utbetalinger);

        assertThat(lists.size(), is(3));
        assertThat(lists.get(0).size(), is(2));
        assertThat(lists.get(1).size(), is(1));
        assertThat(lists.get(2).size(), is(1));
    }

    @Test
    public void lagListeAvUtbetalingsLister_EnMaaned() throws Exception {
        DateTime februarDato = DateTime.now().withMonthOfYear(1);

        Utbetaling utbetaling1 = new UtbetalingBuilder().setUtbetalingsDato(februarDato).createUtbetaling();
        Utbetaling utbetaling2 = new UtbetalingBuilder().setUtbetalingsDato(februarDato).createUtbetaling();
        List<Utbetaling> utbetalinger = asList(utbetaling1, utbetaling2);

        List<List<Utbetaling>> lists = splittUtbetalingerPerMaaned(utbetalinger);

        assertThat(lists.size(), is(1));
        assertThat(lists.get(0).size(), is(2));
    }

}

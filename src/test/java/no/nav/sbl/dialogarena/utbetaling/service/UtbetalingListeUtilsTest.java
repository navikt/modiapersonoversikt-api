package no.nav.sbl.dialogarena.utbetaling.service;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.getBuilder;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils.hentUtbetalingerFraPeriode;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils.hentYtelser;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils.grupperPaaHovedytelseOgPeriode;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.UtbetalingListeUtils.splittUtbetalingerPerMaaned;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class UtbetalingListeUtilsTest {

    private static final String DAGPENGER = "Dagpenger";
    private static final String SYKEPENGER = "Sykepenger";
    private static final String BARNETRYGD = "Barnetrygd";
    private static final String JAN_2012_NR1 = "1. jan 2012 nr1";
    private static final String JAN_2012_NR2 = "1. jan 2012 nr2";
    private static final String MAR_2012_NR1 = "1. mar 2012 nr1";
    private static final String SEP_2012_NR1 = "1. sep 2012 nr1";
    private static final DateTime JAN_2012_DATE = new DateTime(2012, 1, 1, 0, 0);
    private static final DateTime MAR_2012_DATE = new DateTime(2012, 3, 1, 0, 0);
    private static final DateTime SEPT_2012_DATE = new DateTime(2012, 9, 1, 0, 0);

    private List<Utbetaling> utbetalingsliste;

    @Before
    public void settOppUtbetalingsliste() {
        Utbetaling utbetaling1 = getBuilder().withUtbetalingsDato(JAN_2012_DATE).withMelding(JAN_2012_NR1).withHovedytelse(DAGPENGER).createUtbetaling();
        Utbetaling utbetaling2 = getBuilder().withUtbetalingsDato(JAN_2012_DATE).withMelding(JAN_2012_NR2).withHovedytelse(SYKEPENGER).createUtbetaling();
        Utbetaling utbetaling3 = getBuilder().withUtbetalingsDato(MAR_2012_DATE).withMelding(MAR_2012_NR1).withHovedytelse(SYKEPENGER).createUtbetaling();
        Utbetaling utbetaling4 = getBuilder().withUtbetalingsDato(SEPT_2012_DATE).withMelding(SEP_2012_NR1).withHovedytelse(BARNETRYGD).createUtbetaling();

        utbetalingsliste = asList(utbetaling1, utbetaling2, utbetaling3, utbetaling4);
    }

    @Test
    public void hentYtelser_inneholderNoyaktigAlleHovedYtelser() {
        Set<String> ytelser = hentYtelser(utbetalingsliste);

        assertThat(ytelser.size(), is(3));
        assertThat(ytelser, containsInAnyOrder(DAGPENGER, SYKEPENGER, BARNETRYGD));
    }

    @Test
    public void splittUtbetalingerPerMaaned_splittetIRiktigAntallMaanederOverToAar() {
        ArrayList<Utbetaling> utbetalinger = new ArrayList<>(utbetalingsliste);
        Utbetaling utbetaling = getBuilder().withUtbetalingsDato(new DateTime(2014, 1, 1, 0, 0)).withMelding("1. jan 2014 nr1").createUtbetaling();
        utbetalinger.add(utbetaling);

        assertThat(splittUtbetalingerPerMaaned(utbetalinger).size(), is(4));
    }

    @Test
    public void splittUtbetalingerPerMaaned_splittetIRiktigAntallMaaneder() {
        List<List<Utbetaling>> maanedsliste = splittUtbetalingerPerMaaned(utbetalingsliste);

        assertThat(maanedsliste.size(), is(3));
    }

    @Test
    public void splittUtbetalingerPerMaaned_hverMaanedHarRiktigAntallUtbetalinger() {
        List<List<Utbetaling>> maanedsliste = splittUtbetalingerPerMaaned(utbetalingsliste);

        assertThat(maanedsliste.get(0).size(), is(1));
        assertThat(maanedsliste.get(1).size(), is(1));
        assertThat(maanedsliste.get(2).size(), is(2));
    }

    @Test
    public void splittUtbetalingerPerMaaned_inneholderRiktigUtbetalingPerMaaned() {
        List<List<Utbetaling>> maanedsliste = splittUtbetalingerPerMaaned(utbetalingsliste);

        assertThat(maanedsliste.get(0).get(0).getMelding(), is(SEP_2012_NR1));
        assertThat(maanedsliste.get(1).get(0).getMelding(), is(MAR_2012_NR1));
        assertThat(maanedsliste.get(2).get(0).getMelding(), is(JAN_2012_NR1));
    }

    @Test
    public void hentUtbetalingerFraPeriode_inneholderRiktigAntallUtbetalinger() {
        List<Utbetaling> utbetalingsperiode = hentUtbetalingerFraPeriode(utbetalingsliste, JAN_2012_DATE.toLocalDate(), MAR_2012_DATE.toLocalDate());

        assertThat(utbetalingsperiode.size(), is(3));
    }

    @Test
    public void hentUtbetalingerFraPeriode_inneholderKunUtbetalingerInnenforPeriode() {
        DateTime startDato = now().minusMonths(2);
        DateTime sluttDato = now();
        Interval intervall = new Interval(startDato, sluttDato.plusDays(1));

        List<Utbetaling> utbetalingsperiode = hentUtbetalingerFraPeriode(utbetalingsliste, startDato.toLocalDate(), sluttDato.toLocalDate());

        for (Utbetaling utbetaling : utbetalingsperiode) {
            assertTrue(intervall.contains(utbetaling.getUtbetalingsdato()));
        }
    }

    @Test
    public void skalSkillePaaUtbetalingerMedForskjelligHovedytelse() {
        List<Utbetaling> utbetalinger = asList(lagUtbetaling("ytelse1"), lagUtbetaling("ytelse2"));
        List<List<Utbetaling>> resultat = grupperPaaHovedytelseOgPeriode(utbetalinger);
        assertEquals(2, resultat.size());
    }

    @Test
    public void skalSamleYtelserISammePeriode() {
        String ytelse = "Ytelse";
        List<Utbetaling> utbetalinger = asList(
                lagUtbetaling(ytelse, "01.01.2012", "01.02.2012"),
                lagUtbetaling(ytelse, "01.03.2012", "01.04.2012"),
                lagUtbetaling(ytelse, "15.01.2012", "15.03.2012"));
        List<List<Utbetaling>> resultat = grupperPaaHovedytelseOgPeriode(utbetalinger);
        assertEquals(1, resultat.size());
    }

    @Test
    public void skalSkilleMellomYtelserIUlikePerioder() {
        String ytelse = "Ytelse";
        List<Utbetaling> utbetalinger = asList(
                lagUtbetaling(ytelse, "01.01.2012", "01.02.2012"),
                lagUtbetaling(ytelse, "01.01.2013", "01.02.2013"));
        List<List<Utbetaling>> resultat = grupperPaaHovedytelseOgPeriode(utbetalinger);
        assertEquals(2, resultat.size());
    }

    @Test
    public void dagenEtterTellerSomSammePeriode() {
        String ytelse = "ytelse";
        List<Utbetaling> utbetalinger = asList(
                lagUtbetaling(ytelse, "01.01.2012", "31.01.2012"),
                lagUtbetaling(ytelse, "01.02.2012", "28.02.2012"));
        List<List<Utbetaling>> resultat = grupperPaaHovedytelseOgPeriode(utbetalinger);
        assertEquals(1, resultat.size());
    }

    @Test
    public void toDagerEtterErForMye() {
        String ytelse = "ytelse";
        List<Utbetaling> utbetalinger = asList(
                lagUtbetaling(ytelse, "01.01.2012", "14.01.2012"),
                lagUtbetaling(ytelse, "16.01.2012", "31.01.2012"));
        List<List<Utbetaling>> resultat = grupperPaaHovedytelseOgPeriode(utbetalinger);
        assertEquals(2, resultat.size());
    }

    private Utbetaling lagUtbetaling(String hovedytelse) {
        return lagUtbetaling(hovedytelse, DateTime.now(), DateTime.now());
    }

    private Utbetaling lagUtbetaling(String hovedytelse, String fom, String tom) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yyyy");
        return lagUtbetaling(hovedytelse, formatter.parseDateTime(fom), formatter.parseDateTime(tom));
    }

    private Utbetaling lagUtbetaling(String hovedytelse, DateTime fom, DateTime tom) {
        return getBuilder().withHovedytelse(hovedytelse).withPeriode(new Interval(fom, tom)).createUtbetaling();
    }

}

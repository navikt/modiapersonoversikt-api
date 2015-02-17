package no.nav.sbl.dialogarena.utbetaling.service;

import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
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
import static no.nav.sbl.dialogarena.utbetaling.domain.util.HovedytelseUtils.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.*;

public class HovedytelseUtilsTest {

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
    private static final String ID = "id";

    private List<Record<Hovedytelse>> hovedytelseListe;

    @Before
    public void settOppUtbetalingsliste() {
        hovedytelseListe = asList(
                new Record<Hovedytelse>()
                        .with(Hovedytelse.hovedytelsedato, JAN_2012_DATE)
                        .with(Hovedytelse.utbetalingsmelding, JAN_2012_NR1)
                        .with(Hovedytelse.ytelse, DAGPENGER),
                new Record<Hovedytelse>()
                        .with(Hovedytelse.hovedytelsedato, JAN_2012_DATE)
                        .with(Hovedytelse.utbetalingsmelding, JAN_2012_NR2)
                        .with(Hovedytelse.ytelse, SYKEPENGER),
                new Record<Hovedytelse>()
                        .with(Hovedytelse.hovedytelsedato, MAR_2012_DATE)
                        .with(Hovedytelse.utbetalingsmelding, MAR_2012_NR1)
                        .with(Hovedytelse.ytelse, SYKEPENGER),
                new Record<Hovedytelse>()
                        .with(Hovedytelse.hovedytelsedato, SEPT_2012_DATE)
                        .with(Hovedytelse.utbetalingsmelding, SEP_2012_NR1)
                        .with(Hovedytelse.ytelse, BARNETRYGD)
        );
    }

    @Test
    public void hentYtelser_inneholderNoyaktigAlleHovedYtelser() {
        Set<String> ytelser = hovedytelseToYtelsebeskrivelse(hovedytelseListe);

        assertThat(ytelser.size(), is(3));
        assertThat(ytelser, containsInAnyOrder(DAGPENGER, SYKEPENGER, BARNETRYGD));
    }

    @Test
    public void splittUtbetalingerPerMaaned_splittetIRiktigAntallMaanederOverToAar() {
        List<Record<Hovedytelse>> ytelseListe = new ArrayList<>(hovedytelseListe);

        Record<Hovedytelse> ytelse = new Record<Hovedytelse>()
                .with(Hovedytelse.hovedytelsedato, new DateTime(2014, 1, 1, 0, 0))
                .with(Hovedytelse.utbetalingsmelding, "1. jan 2014 nr1");

        ytelseListe.add(ytelse);

        assertThat(splittUtbetalingerPerMaaned(ytelseListe).size(), is(4));
    }

    @Test
    public void splittUtbetalingerPerMaaned_splittetIRiktigAntallMaaneder() {
        List<List<Record<Hovedytelse>>> maanedsMap = splittUtbetalingerPerMaaned(hovedytelseListe);

        assertThat(maanedsMap.size(), is(3));
    }

    @Test
    public void splittUtbetalingerPerMaaned_hverMaanedHarRiktigAntallUtbetalinger() {
        List<List<Record<Hovedytelse>>> maanedsMap = splittUtbetalingerPerMaaned(hovedytelseListe);

        assertThat(maanedsMap.get(0).size(), is(1));
        assertThat(maanedsMap.get(1).size(), is(1));
        assertThat(maanedsMap.get(2).size(), is(2));
    }

    @Test
    public void splittUtbetalingerPerMaaned_inneholderRiktigUtbetalingPerMaaned() {
        List<List<Record<Hovedytelse>>> maanedsMap  = splittUtbetalingerPerMaaned(hovedytelseListe);

        assertThat(maanedsMap.get(0).get(0).get(Hovedytelse.utbetalingsmelding), is(SEP_2012_NR1));
        assertThat(maanedsMap.get(1).get(0).get(Hovedytelse.utbetalingsmelding), is(MAR_2012_NR1));
        assertThat(maanedsMap.get(2).get(0).get(Hovedytelse.utbetalingsmelding), is(JAN_2012_NR1));
    }

    @Test
    public void hentUtbetalingerFraPeriode_inneholderRiktigAntallUtbetalinger() {
        List<Record<Hovedytelse>> utbetalingsperiode = hentHovedytelserFraPeriode(hovedytelseListe, JAN_2012_DATE.toLocalDate(), MAR_2012_DATE.toLocalDate());

        assertThat(utbetalingsperiode.size(), is(3));
    }

    @Test
    public void hentUtbetalingerFraPeriode_inneholderKunUtbetalingerInnenforPeriode() {
        DateTime startDato = now().minusMonths(2);
        DateTime sluttDato = now();
        Interval intervall = new Interval(startDato, sluttDato.plusDays(1));

        List<Record<Hovedytelse>> utbetalingsperiode = hentHovedytelserFraPeriode(hovedytelseListe, startDato.toLocalDate(), sluttDato.toLocalDate());

        for (Record<Hovedytelse> hovedytelse : utbetalingsperiode) {
            assertTrue(intervall.contains(hovedytelse.get(Hovedytelse.hovedytelsedato)));
        }
    }

    @Test
    public void skalSkillePaaUtbetalingerMedForskjelligHovedytelse() {
        List<Record<Hovedytelse>> ytelser = asList(lagHovedytelse("ytelse1"), lagHovedytelse("ytelse2"));
        List<List<Record<Hovedytelse>>> resultat = grupperPaaHovedytelseOgPeriode(ytelser);
        assertEquals(2, resultat.size());
    }

    @Test
    public void skalSamleYtelserISammePeriode() {
        String ytelse = "Ytelse";
        List<Record<Hovedytelse>> ytelser = asList(
                lagHovedytelse(ytelse, "01.01.2012", "01.02.2012"),
                lagHovedytelse(ytelse, "01.03.2012", "01.04.2012"),
                lagHovedytelse(ytelse, "15.01.2012", "15.03.2012"));
        List<List<Record<Hovedytelse>>> resultat = grupperPaaHovedytelseOgPeriode(ytelser);
        assertEquals(1, resultat.size());
    }

    @Test
    public void skalSkilleMellomYtelserIUlikePerioder() {
        String ytelse = "Ytelse";
        List<Record<Hovedytelse>> ytelser = asList(
                lagHovedytelse(ytelse, "01.01.2012", "01.02.2012"),
                lagHovedytelse(ytelse, "01.01.2013", "01.02.2013"));
        List<List<Record<Hovedytelse>>> resultat = grupperPaaHovedytelseOgPeriode(ytelser);
        assertEquals(2, resultat.size());
    }

    @Test
    public void dagenEtterTellerSomSammePeriode() {
        String ytelse = "ytelse";
        List<Record<Hovedytelse>> ytelser = asList(
                lagHovedytelse(ytelse, "01.01.2012", "31.01.2012"),
                lagHovedytelse(ytelse, "01.02.2012", "28.02.2012"));
        List<List<Record<Hovedytelse>>> resultat = grupperPaaHovedytelseOgPeriode(ytelser);
        assertEquals(1, resultat.size());
    }

    @Test
    public void toDagerEtterErForMye() {
        String ytelse = "ytelse";
        List<Record<Hovedytelse>> ytelser = asList(
                lagHovedytelse(ytelse, "01.01.2012", "14.01.2012"),
                lagHovedytelse(ytelse, "16.01.2012", "31.01.2012"));
        List<List<Record<Hovedytelse>>> resultat = grupperPaaHovedytelseOgPeriode(ytelser);
        assertEquals(2, resultat.size());
    }

    private Record<Hovedytelse> lagHovedytelse(String ytelseBeskrivelse) {
        return lagHovedytelse(ytelseBeskrivelse, DateTime.now(), DateTime.now());
    }

    private Record<Hovedytelse> lagHovedytelse(String ytelseBeskrivelse, String fom, String tom) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yyyy");
        return lagHovedytelse(ytelseBeskrivelse, formatter.parseDateTime(fom), formatter.parseDateTime(tom));
    }

    private Record<Hovedytelse> lagHovedytelse(String ytelsebeskrivelse, DateTime fom, DateTime tom) {
        return new Record<Hovedytelse>()
                .with(Hovedytelse.ytelse, ytelsebeskrivelse)
                .with(Hovedytelse.ytelsesperiode, new Interval(fom, tom));
    }

}

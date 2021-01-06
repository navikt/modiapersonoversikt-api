package no.nav.sbl.dialogarena.utbetaling.service;

import no.nav.sbl.dialogarena.utbetaling.domain.Hovedutbetaling;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.YearMonth;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.DateUtils.intervalFromStartEndDate;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;

public class YtelseUtilsTest {

    private static final String DAGPENGER = "Dagpenger";
    private static final String SYKEPENGER = "Sykepenger";
    private static final String BARNETRYGD = "Barnetrygd";
    private static final String JAN_2012_NR1 = "1. jan 2012 nr1";
    private static final String JAN_2012_NR2 = "1. jan 2012 nr2";
    private static final String MAR_2012_NR1 = "1. mar 2012 nr1";
    private static final String SEP_2012_NR1 = "1. sep 2012 nr1";
    private static final DateTime JAN_2012_DATE = new DateTime(2012, 1, 1, 0, 0);
    private static final DateTime JAN_2012_DATE_2 = new DateTime(2012, 1, 2, 0, 0);
    private static final DateTime MAR_2012_DATE = new DateTime(2012, 3, 1, 0, 0);
    private static final DateTime SEPT_2012_DATE = new DateTime(2012, 9, 1, 0, 0);

    private List<Hovedytelse> hovedytelseListe;
    private List<Hovedutbetaling> hovedutbetalinger;

    @Before
    public void settOppUtbetalingsliste() {
        Hovedytelse hovedytelseJanuar = new Hovedytelse()
                .withHovedytelsedato(JAN_2012_DATE_2)
                .withPosteringsDato(JAN_2012_DATE_2)
                .withUtbetalingsmelding(JAN_2012_NR1)
                .withYtelse(DAGPENGER);

        Hovedytelse hovedytelseJanuar2 = new Hovedytelse()
                .withHovedytelsedato(JAN_2012_DATE)
                .withPosteringsDato(JAN_2012_DATE)
                .withUtbetalingsmelding(JAN_2012_NR2)
                .withYtelse(SYKEPENGER);
        Hovedytelse hovedytelseMars = new Hovedytelse()
                .withHovedytelsedato(MAR_2012_DATE)
                .withPosteringsDato(MAR_2012_DATE)
                .withUtbetalingsmelding(MAR_2012_NR1)
                .withYtelse(SYKEPENGER);
        Hovedytelse hovedytelseSeptember = new Hovedytelse()
                .withHovedytelsedato(SEPT_2012_DATE)
                .withPosteringsDato(SEPT_2012_DATE)
                .withUtbetalingsmelding(SEP_2012_NR1)
                .withYtelse(BARNETRYGD);
        hovedytelseListe = asList(
                hovedytelseJanuar,
                hovedytelseJanuar2,
                hovedytelseMars,
                hovedytelseSeptember);

        hovedutbetalinger = asList(
                new Hovedutbetaling().withHovedytelser(singletonList(hovedytelseJanuar)).withHovedytelsesdato(JAN_2012_DATE),
                new Hovedutbetaling().withHovedytelser(singletonList(hovedytelseJanuar2)).withHovedytelsesdato(JAN_2012_DATE_2),
                new Hovedutbetaling().withHovedytelser(singletonList(hovedytelseMars)).withHovedytelsesdato(MAR_2012_DATE),
                new Hovedutbetaling().withHovedytelser(singletonList(hovedytelseSeptember)).withHovedytelsesdato(SEPT_2012_DATE)
        );
    }

    @Test
    public void hentYtelser_inneholderNoyaktigAlleHovedYtelser() {
        Set<String> ytelser = hovedytelseListe.stream()
                .map(Hovedytelse::getYtelse)
                .collect(toSet());

        assertThat(ytelser.size(), is(3));
        assertThat(ytelser, containsInAnyOrder(DAGPENGER, SYKEPENGER, BARNETRYGD));
    }

    @Test
    public void ytelserGroupedByYearMonth_sortertSynkende() {
        List<Hovedutbetaling> hovedutbetalinger = new ArrayList<>(asList(
                new Hovedutbetaling().withHovedytelsesdato(new DateTime(2015, 1, 1, 1, 1)),
                new Hovedutbetaling().withHovedytelsesdato(new DateTime(2015, 3, 1, 1, 1)),
                new Hovedutbetaling().withHovedytelsesdato(new DateTime(2015, 2, 1, 1, 1))
        ));


        Map<YearMonth, List<Hovedutbetaling>> yearMonthListMap = YtelseUtils.hovedutbetalingerGroupedByYearMonth(hovedutbetalinger);

        Iterator<Map.Entry<YearMonth, List<Hovedutbetaling>>> iterator = yearMonthListMap.entrySet().iterator();
        assertThat(iterator.next().getKey(), is(new YearMonth(2015, 3)));
        assertThat(iterator.next().getKey(), is(new YearMonth(2015, 2)));
        assertThat(iterator.next().getKey(), is(new YearMonth(2015, 1)));
    }

    @Test
    public void splittUtbetalingerPerMaaned_splittetIRiktigAntallMaanederOverToAar() {
        List<Hovedutbetaling> hovedutbetalinger = new ArrayList<>(this.hovedutbetalinger);

        Hovedutbetaling utbetaling = new Hovedutbetaling()
                .withHovedytelser(singletonList(new Hovedytelse()
                        .withHovedytelsedato(new DateTime(2014, 1, 1, 0, 0))
                        .withUtbetalingsmelding("1. jan 2014 nr1")))
                .withHovedytelsesdato(new DateTime(2014, 1, 1, 0, 0));

        hovedutbetalinger.add(utbetaling);

        assertThat(hovedutbetalingerGroupedByYearMonth(hovedutbetalinger).size(), is(4));
    }

    @Test
    public void splittUtbetalingerPerMaaned_splittetIRiktigAntallMaaneder() {
        Map<YearMonth, List<Hovedutbetaling>> maanedsMap = hovedutbetalingerGroupedByYearMonth(hovedutbetalinger);
        assertThat(maanedsMap.size(), is(3));
    }

    @Test
    public void splittUtbetalingerPerMaaned_hverMaanedHarRiktigAntallUtbetalinger() {
        Map<YearMonth, List<Hovedutbetaling>> maanedsMap = hovedutbetalingerGroupedByYearMonth(hovedutbetalinger);

        assertThat(maanedsMap.get(new YearMonth(2012, 9)).size(), is(1));
        assertThat(maanedsMap.get(new YearMonth(2012, 3)).size(), is(1));
        assertThat(maanedsMap.get(new YearMonth(2012, 1)).size(), is(2));
    }

    @Test
    public void splittUtbetalingerPerMaaned_inneholderRiktigUtbetalingPerMaaned() {
        Map<YearMonth, List<Hovedutbetaling>> maanedsMap = hovedutbetalingerGroupedByYearMonth(hovedutbetalinger);

        assertThat(maanedsMap.get(new YearMonth(2012, 9)).get(0).getHovedytelser().get(0).getUtbetalingsmelding(), is(SEP_2012_NR1));
        assertThat(maanedsMap.get(new YearMonth(2012, 3)).get(0).getHovedytelser().get(0).getUtbetalingsmelding(), is(MAR_2012_NR1));
        assertThat(maanedsMap.get(new YearMonth(2012, 1)).get(0).getHovedytelser().get(0).getUtbetalingsmelding(), is(JAN_2012_NR1));
    }

    @Test
    public void hentUtbetalingerFraPeriode_inneholderRiktigAntallUtbetalinger() {

        Interval intervall = intervalFromStartEndDate(JAN_2012_DATE.toLocalDate(), MAR_2012_DATE.toLocalDate());
        List<Hovedytelse> utbetalingsperiode = hovedytelserInnenforIntervall(hovedytelseListe, intervall);

        assertThat(utbetalingsperiode.size(), is(3));
    }

    @Test
    public void hentUtbetalingerFraPeriode_inneholderKunUtbetalingerInnenforPeriode() {
        DateTime startDato = new DateTime(2012, 1, 2, 0, 0, 0);
        DateTime sluttDato = new DateTime(2012, 3, 1, 0, 0, 0);

        Interval intervall = intervalFromStartEndDate(startDato.toLocalDate(), sluttDato.toLocalDate());

        List<Hovedytelse> utbetalingsperiode = hovedytelserInnenforIntervall(hovedytelseListe, intervall);

        assertThat(utbetalingsperiode.size(), is(2));
    }

    @Test
    public void skalSkillePaaUtbetalingerMedForskjelligHovedytelse() {
        List<Hovedytelse> ytelser = asList(lagHovedytelse("ytelse1", now(), now()), lagHovedytelse("ytelse2", now(), now()));
        List<List<Hovedytelse>> resultat = groupByHovedytelseAndPeriod(ytelser);
        assertEquals(2, resultat.size());
    }

    @Test
    public void skalSamleYtelserISammePeriode() {
        String ytelse = "Ytelse";
        List<Hovedytelse> ytelser = asList(
                lagHovedytelse(ytelse, "01.01.2012", "01.02.2012"),
                lagHovedytelse(ytelse, "01.03.2012", "01.04.2012"),
                lagHovedytelse(ytelse, "15.01.2012", "15.03.2012"));
        List<List<Hovedytelse>> resultat = groupByHovedytelseAndPeriod(ytelser);
        assertEquals(1, resultat.size());
    }

    @Test
    public void skalSkilleMellomYtelserIUlikePerioder() {
        String ytelse = "Ytelse";
        List<Hovedytelse> ytelser = asList(
                lagHovedytelse(ytelse, "01.01.2012", "01.02.2012"),
                lagHovedytelse(ytelse, "01.01.2013", "01.02.2013"));
        List<List<Hovedytelse>> resultat = groupByHovedytelseAndPeriod(ytelser);
        assertEquals(2, resultat.size());
    }

    @Test
    public void dagenEtterTellerSomSammePeriode() {
        String ytelse = "ytelse";
        List<Hovedytelse> ytelser = asList(
                lagHovedytelse(ytelse, "01.01.2012", "31.01.2012"),
                lagHovedytelse(ytelse, "01.02.2012", "28.02.2012"));
        List<List<Hovedytelse>> resultat = groupByHovedytelseAndPeriod(ytelser);
        assertEquals(1, resultat.size());
    }

    @Test
    public void toDagerEtterErForMye() {
        String ytelse = "ytelse";
        List<Hovedytelse> ytelser = asList(
                lagHovedytelse(ytelse, "01.01.2012", "14.01.2012"),
                lagHovedytelse(ytelse, "16.01.2012", "31.01.2012"));
        List<List<Hovedytelse>> resultat = groupByHovedytelseAndPeriod(ytelser);
        assertEquals(2, resultat.size());
    }

    @Test
    public void sortByHovedytelsedato() {
        Hovedytelse hovedytelseA = new Hovedytelse().withHovedytelsedato(new DateTime(2015, 1, 2, 1, 1));
        Hovedytelse hovedytelseB = new Hovedytelse().withHovedytelsedato(new DateTime(2015, 1, 1, 1, 1));
        Hovedytelse hovedytelseC = new Hovedytelse().withHovedytelsedato(new DateTime(2015, 1, 3, 1, 1));

        List<Hovedytelse> unsortedList = asList(hovedytelseA, hovedytelseB, hovedytelseC);

        List<Hovedytelse> sortedList = unsortedList.stream().sorted(YtelseUtils.SISTE_HOVEDYTELSESDATO_FORST).collect(toList());
        assertThat(sortedList.get(0).getHovedytelsedato(), is(new DateTime(2015, 1, 3, 1, 1)));
        assertThat(sortedList.get(1).getHovedytelsedato(), is(new DateTime(2015, 1, 2, 1, 1)));
        assertThat(sortedList.get(2).getHovedytelsedato(), is(new DateTime(2015, 1, 1, 1, 1)));
    }

    @Test
    public void sortererYtelserUtenPeriodeSammen() {
        String ytelse = "Ytelse";
        List<Hovedytelse> ytelser = asList(
                lagHovedytelse(ytelse, "01.01.2012", "01.02.2012"),
                lagHovedytelseUtenPeriode(ytelse),
                lagHovedytelseUtenPeriode(ytelse));
        List<List<Hovedytelse>> resultat = groupByHovedytelseAndPeriod(ytelser);
        assertEquals(2, resultat.size());
    }

    private Hovedytelse lagHovedytelseUtenPeriode(String ytelseBeskrivelse) {
        return lagHovedytelse(ytelseBeskrivelse, new DateTime(0), new DateTime(0));
    }

    private Hovedytelse lagHovedytelse(String ytelseBeskrivelse, String fom, String tom) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yyyy");
        return lagHovedytelse(ytelseBeskrivelse, formatter.parseDateTime(fom), formatter.parseDateTime(tom));
    }

    private Hovedytelse lagHovedytelse(String ytelsebeskrivelse, DateTime fom, DateTime tom) {
        return new Hovedytelse()
                .withYtelse(ytelsebeskrivelse)
                .withYtelsesperiode(new Interval(fom, tom));
    }

    @Test
    public void sortererYtelserMedSammeHovedytelsedatoPaaYtelsen() {
        List<Hovedytelse> ytelser = asList(new Hovedytelse()
                        .withHovedytelsedato(new DateTime(2015, 1, 1, 1, 1))
                        .withYtelse("Aytelse"),
                new Hovedytelse()
                        .withHovedytelsedato(new DateTime(2015, 1, 1, 1, 1))
                        .withYtelse("Cytelse"),
                new Hovedytelse()
                        .withHovedytelsedato(new DateTime(2015, 1, 1, 1, 1))
                        .withYtelse("Bytelse"));
        List<Hovedytelse> sortedYtelser = ytelser.stream().sorted(SISTE_HOVEDYTELSESDATO_FORST).collect(toList());

        assertThat(sortedYtelser.get(0).getYtelse(), is("Aytelse"));
        assertThat(sortedYtelser.get(1).getYtelse(), is("Bytelse"));
        assertThat(sortedYtelser.get(2).getYtelse(), is("Cytelse"));
    }

    @Test
    public void korrektDefaultSluttDato() {
        DateTime sluttDato = now().plusDays(ANTALL_DAGER_FRAMOVER_I_TID);
        assertThat(YtelseUtils.defaultSluttDato().getYear(), is(sluttDato.getYear()));
        assertThat(YtelseUtils.defaultSluttDato().getMonthOfYear(), is(sluttDato.getMonthOfYear()));
        assertThat(YtelseUtils.defaultSluttDato().getDayOfMonth(), is(sluttDato.getDayOfMonth()));
    }

    @Test
    public void korrektDefaultStartDato() {
        DateTime startDato = now().minusDays(30);
        assertThat(YtelseUtils.defaultStartDato().getYear(), is(startDato.getYear()));
        assertThat(YtelseUtils.defaultStartDato().getMonthOfYear(), is(startDato.getMonthOfYear()));
        assertThat(YtelseUtils.defaultStartDato().getDayOfMonth(), is(startDato.getDayOfMonth()));
    }
}

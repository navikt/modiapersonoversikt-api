package no.nav.sbl.dialogarena.utbetaling.service;

import no.nav.sbl.dialogarena.common.records.Record;
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
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse.hovedytelsedato;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.*;

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

    private List<Record<Hovedytelse>> hovedytelseListe;

    @Before
    public void settOppUtbetalingsliste() {
        hovedytelseListe = asList(
                new Record<Hovedytelse>()
                        .with(hovedytelsedato, JAN_2012_DATE_2)
                        .with(Hovedytelse.utbetalingsmelding, JAN_2012_NR1)
                        .with(Hovedytelse.ytelse, DAGPENGER),
                new Record<Hovedytelse>()
                        .with(hovedytelsedato, JAN_2012_DATE)
                        .with(Hovedytelse.utbetalingsmelding, JAN_2012_NR2)
                        .with(Hovedytelse.ytelse, SYKEPENGER),
                new Record<Hovedytelse>()
                        .with(hovedytelsedato, MAR_2012_DATE)
                        .with(Hovedytelse.utbetalingsmelding, MAR_2012_NR1)
                        .with(Hovedytelse.ytelse, SYKEPENGER),
                new Record<Hovedytelse>()
                        .with(hovedytelsedato, SEPT_2012_DATE)
                        .with(Hovedytelse.utbetalingsmelding, SEP_2012_NR1)
                        .with(Hovedytelse.ytelse, BARNETRYGD));
    }

    @Test
    public void hentYtelser_inneholderNoyaktigAlleHovedYtelser() {
        Set<String> ytelser = on(hovedytelseListe).map(Hovedytelse.ytelse).collectIn(new HashSet<String>());

        assertThat(ytelser.size(), is(3));
        assertThat(ytelser, containsInAnyOrder(DAGPENGER, SYKEPENGER, BARNETRYGD));
    }

    @Test
    public void ytelserGroupedByYearMonth_sortertSynkende() {
        List<Record<Hovedytelse>> ytelseListe = new ArrayList<>(asList(
                new Record<Hovedytelse>().with(Hovedytelse.hovedytelsedato, new DateTime(2015, 01, 1, 1, 1)),
                new Record<Hovedytelse>().with(Hovedytelse.hovedytelsedato, new DateTime(2015, 03, 1, 1, 1)),
                new Record<Hovedytelse>().with(Hovedytelse.hovedytelsedato, new DateTime(2015, 02, 1, 1, 1))
        ));

        Map<YearMonth, List<Record<Hovedytelse>>> yearMonthListMap = YtelseUtils.ytelserGroupedByYearMonth(ytelseListe);
        Iterator<Map.Entry<YearMonth, List<Record<Hovedytelse>>>> iterator = yearMonthListMap.entrySet().iterator();
        assertThat(iterator.next().getKey(), is(new YearMonth(2015, 03)));
        assertThat(iterator.next().getKey(), is(new YearMonth(2015, 02)));
        assertThat(iterator.next().getKey(), is(new YearMonth(2015, 01)));
    }

    @Test
    public void splittUtbetalingerPerMaaned_splittetIRiktigAntallMaanederOverToAar() {
        List<Record<Hovedytelse>> ytelseListe = new ArrayList<>(hovedytelseListe);

        Record<Hovedytelse> ytelse = new Record<Hovedytelse>()
                .with(hovedytelsedato, new DateTime(2014, 1, 1, 0, 0))
                .with(Hovedytelse.utbetalingsmelding, "1. jan 2014 nr1");

        ytelseListe.add(ytelse);

        assertThat(ytelserGroupedByYearMonth(ytelseListe).size(), is(4));
    }

    @Test
    public void splittUtbetalingerPerMaaned_splittetIRiktigAntallMaaneder() {
        Map<YearMonth, List<Record<Hovedytelse>>> maanedsMap = ytelserGroupedByYearMonth(hovedytelseListe);
        assertThat(maanedsMap.size(), is(3));
    }

    @Test
    public void splittUtbetalingerPerMaaned_hverMaanedHarRiktigAntallUtbetalinger() {
        Map<YearMonth, List<Record<Hovedytelse>>> maanedsMap = ytelserGroupedByYearMonth(hovedytelseListe);

        assertThat(maanedsMap.get(new YearMonth(2012, 9)).size(), is(1));
        assertThat(maanedsMap.get(new YearMonth(2012, 3)).size(), is(1));
        assertThat(maanedsMap.get(new YearMonth(2012, 1)).size(), is(2));
    }

    @Test
    public void splittUtbetalingerPerMaaned_inneholderRiktigUtbetalingPerMaaned() {
        Map<YearMonth, List<Record<Hovedytelse>>> maanedsMap = ytelserGroupedByYearMonth(hovedytelseListe);

        assertThat(maanedsMap.get(new YearMonth(2012, 9)).get(0).get(Hovedytelse.utbetalingsmelding), is(SEP_2012_NR1));
        assertThat(maanedsMap.get(new YearMonth(2012, 3)).get(0).get(Hovedytelse.utbetalingsmelding), is(MAR_2012_NR1));
        assertThat(maanedsMap.get(new YearMonth(2012, 1)).get(0).get(Hovedytelse.utbetalingsmelding), is(JAN_2012_NR1));
    }

    @Test
    public void hentUtbetalingerFraPeriode_inneholderRiktigAntallUtbetalinger() {
        List<Record<Hovedytelse>> utbetalingsperiode = hovedytelserFromPeriod(hovedytelseListe, JAN_2012_DATE.toLocalDate(), MAR_2012_DATE.toLocalDate());

        assertThat(utbetalingsperiode.size(), is(3));
    }

    @Test
    public void hentUtbetalingerFraPeriode_inneholderKunUtbetalingerInnenforPeriode() {
        DateTime startDato = now().minusMonths(2);
        DateTime sluttDato = now();
        Interval intervall = new Interval(startDato, sluttDato.plusDays(1));

        List<Record<Hovedytelse>> utbetalingsperiode = hovedytelserFromPeriod(hovedytelseListe, startDato.toLocalDate(), sluttDato.toLocalDate());

        for (Record<Hovedytelse> hovedytelse : utbetalingsperiode) {
            assertTrue(intervall.contains(hovedytelse.get(hovedytelsedato)));
        }
    }

    @Test
    public void skalSkillePaaUtbetalingerMedForskjelligHovedytelse() {
        List<Record<Hovedytelse>> ytelser = asList(lagHovedytelse("ytelse1", now(), now()), lagHovedytelse("ytelse2", now(), now()));
        List<List<Record<Hovedytelse>>> resultat = groupByHovedytelseAndPeriod(ytelser);
        assertEquals(2, resultat.size());
    }

    @Test
    public void skalSamleYtelserISammePeriode() {
        String ytelse = "Ytelse";
        List<Record<Hovedytelse>> ytelser = asList(
                lagHovedytelse(ytelse, "01.01.2012", "01.02.2012"),
                lagHovedytelse(ytelse, "01.03.2012", "01.04.2012"),
                lagHovedytelse(ytelse, "15.01.2012", "15.03.2012"));
        List<List<Record<Hovedytelse>>> resultat = groupByHovedytelseAndPeriod(ytelser);
        assertEquals(1, resultat.size());
    }

    @Test
    public void skalSkilleMellomYtelserIUlikePerioder() {
        String ytelse = "Ytelse";
        List<Record<Hovedytelse>> ytelser = asList(
                lagHovedytelse(ytelse, "01.01.2012", "01.02.2012"),
                lagHovedytelse(ytelse, "01.01.2013", "01.02.2013"));
        List<List<Record<Hovedytelse>>> resultat = groupByHovedytelseAndPeriod(ytelser);
        assertEquals(2, resultat.size());
    }

    @Test
    public void dagenEtterTellerSomSammePeriode() {
        String ytelse = "ytelse";
        List<Record<Hovedytelse>> ytelser = asList(
                lagHovedytelse(ytelse, "01.01.2012", "31.01.2012"),
                lagHovedytelse(ytelse, "01.02.2012", "28.02.2012"));
        List<List<Record<Hovedytelse>>> resultat = groupByHovedytelseAndPeriod(ytelser);
        assertEquals(1, resultat.size());
    }

    @Test
    public void toDagerEtterErForMye() {
        String ytelse = "ytelse";
        List<Record<Hovedytelse>> ytelser = asList(
                lagHovedytelse(ytelse, "01.01.2012", "14.01.2012"),
                lagHovedytelse(ytelse, "16.01.2012", "31.01.2012"));
        List<List<Record<Hovedytelse>>> resultat = groupByHovedytelseAndPeriod(ytelser);
        assertEquals(2, resultat.size());
    }

    @Test
    public void sortByHovedytelsedato() {
        Record<Hovedytelse> hovedytelseA = new Record<Hovedytelse>().with(hovedytelsedato, new DateTime(2015, 01, 02, 1, 1));
        Record<Hovedytelse> hovedytelseB = new Record<Hovedytelse>().with(hovedytelsedato, new DateTime(2015, 01, 01, 1, 1));
        Record<Hovedytelse> hovedytelseC = new Record<Hovedytelse>().with(hovedytelsedato, new DateTime(2015, 01, 03, 1, 1));

        List<Record<Hovedytelse>> unsortedList = asList(hovedytelseA, hovedytelseB, hovedytelseC);

        List<Record<Hovedytelse>> sortedList = on(unsortedList).collect(YtelseUtils.SORT_BY_HOVEDYTELSEDATO_DESC);
        assertThat(sortedList.get(0).get(hovedytelsedato), is(new DateTime(2015, 01, 03, 1, 1)));
        assertThat(sortedList.get(1).get(hovedytelsedato), is(new DateTime(2015, 01, 02, 1, 1)));
        assertThat(sortedList.get(2).get(hovedytelsedato), is(new DateTime(2015, 01, 01, 1, 1)));
    }

    @Test
    public void sortererYtelserUtenPeriodeSammen() {
        String ytelse = "Ytelse";
        List<Record<Hovedytelse>> ytelser = asList(
                lagHovedytelse(ytelse, "01.01.2012", "01.02.2012"),
                lagHovedytelseUtenPeriode(ytelse),
                lagHovedytelseUtenPeriode(ytelse));
        List<List<Record<Hovedytelse>>> resultat = groupByHovedytelseAndPeriod(ytelser);
        assertEquals(2, resultat.size());
    }

    private Record<Hovedytelse> lagHovedytelseUtenPeriode(String ytelseBeskrivelse) {
        return lagHovedytelse(ytelseBeskrivelse, new DateTime(0), new DateTime(0));
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

    @Test
    public void sortererYtelserMedSammeHovedytelsedatoPaaYtelsen() {
        List<Record<Hovedytelse>> ytelser = asList(new Record<Hovedytelse>()
                        .with(Hovedytelse.hovedytelsedato, new DateTime(2015, 1, 1, 1, 1))
                        .with(Hovedytelse.ytelse, "Aytelse"),
                new Record<Hovedytelse>()
                        .with(Hovedytelse.hovedytelsedato, new DateTime(2015, 1, 1, 1, 1))
                        .with(Hovedytelse.ytelse, "Cytelse"),
                new Record<Hovedytelse>()
                        .with(Hovedytelse.hovedytelsedato, new DateTime(2015, 1, 1, 1, 1))
                        .with(Hovedytelse.ytelse, "Bytelse"));
        List<Record<Hovedytelse>> sortedYtelser = on(ytelser).collect(UtbetalingComparator.HOVEDYTELSE_DATO_COMPARATOR);

        assertThat(sortedYtelser.get(0).get(Hovedytelse.ytelse), is("Aytelse"));
        assertThat(sortedYtelser.get(1).get(Hovedytelse.ytelse), is("Bytelse"));
        assertThat(sortedYtelser.get(2).get(Hovedytelse.ytelse), is("Cytelse"));
    }
}

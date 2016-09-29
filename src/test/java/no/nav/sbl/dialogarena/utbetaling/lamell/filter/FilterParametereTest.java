package no.nav.sbl.dialogarena.utbetaling.lamell.filter;


import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.Mottakertype;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere.PeriodeVelger;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.*;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.*;

public class FilterParametereTest {

    private static final String DAGPENGER = "Dagpenger";
    private static final String BARNETRYGD = "Barnetrygd";
    private static final String ID = "id";

    private FilterParametere filterparams;

    @Before
    public void settOppFilter() {
        filterparams = new FilterParametere(new HashSet<>(asList(DAGPENGER, BARNETRYGD)));
    }

    @Test
    public void filtrererBortUtbetalingForAnnenMottakertype() {
        Hovedytelse ytelse = new Hovedytelse()
                .withId(ID)
                .withHovedytelsedato(now())
                .withMottakertype(Mottakertype.ANNEN_MOTTAKER)
                .withYtelse(DAGPENGER);

        filterparams.toggleMottaker(Mottakertype.ANNEN_MOTTAKER);

        assertFalse(filterparams.test(ytelse));
    }

    @Test
    public void skalBeholdeYtelsenHvisUtbetalingenInneholderEnYtelseManVilHa() {
        Hovedytelse ytelse = new Hovedytelse()
                .withId(ID)
                .withHovedytelsedato(now())
                .withYtelse(BARNETRYGD)
                .withMottakertype(Mottakertype.BRUKER);

        filterparams.leggTilOnsketYtelse(BARNETRYGD);

        assertTrue(filterparams.test(ytelse));
    }

    @Test
    public void skalIkkeBeholdeYtelsenHvisAlleUtbetalingerErUonskede() {
        Hovedytelse ytelse = new Hovedytelse()
                .withId(ID)
                .withHovedytelsedato(now())
                .withMottakertype(Mottakertype.BRUKER)
                .withYtelse(DAGPENGER);

        filterparams.velgEnYtelse(BARNETRYGD);

        assertFalse(filterparams.test(ytelse));
    }

    @Test
    public void skalViseAlleUtbetalingerHvisAlleYtelserErValgtOgNyeYtelserBlirSatt() {
        filterparams = new FilterParametere(new HashSet<>());

        Hovedytelse ytelse = new Hovedytelse()
                .withId(ID)
                .withHovedytelsedato(now())
                .withMottakertype(Mottakertype.BRUKER)
                .withYtelse(DAGPENGER);
        filterparams.setYtelser(new HashSet<>(asList(ytelse.getYtelse())));

        assertThat(filterparams.test(ytelse), is(true));
    }

    @Test
    public void skalToggleVerdiBasertPaaMottaker() {
        assertThat(filterparams.viseMottaker(Mottakertype.BRUKER.BRUKER), is(true));
        filterparams.toggleMottaker(Mottakertype.BRUKER);
        assertThat(filterparams.viseMottaker(Mottakertype.BRUKER), is(false));
        filterparams.toggleMottaker(Mottakertype.BRUKER);
        assertThat(filterparams.viseMottaker(Mottakertype.BRUKER), is(true));
    }

    @Test
    public void skalIkkeSetteDatoerHvisParameterErNull() {
        filterparams.setStartDato(null);
        assertThat(filterparams.getStartDato(), is(defaultStartDato()));

        filterparams.setSluttDato(null);
        assertThat(filterparams.getSluttDato(), is(defaultSluttDato()));
    }

    @Test
    public void skalSetteDatoer() {
        filterparams.periodeVelgerValg = PeriodeVelger.EGENDEFINERT;
        LocalDate startDato = now().toLocalDate();
        filterparams.setStartDato(startDato);
        assertThat(filterparams.getStartDato(), is(startDato));

        LocalDate sluttDato = now().plusDays(1).toLocalDate();
        filterparams.setSluttDato(sluttDato);
        assertThat(filterparams.getSluttDato(), is(sluttDato));
    }

    @Test
    public void skalToggleAlleYtelserSomOnsket() {
        Hovedytelse ytelse = new Hovedytelse()
                .withId(ID)
                .withHovedytelsedato(now())
                .withMottakertype(Mottakertype.BRUKER);

        filterparams.toggleAlleYtelser(true);

        assertThat(filterparams.test(ytelse.withYtelse(DAGPENGER)), is(true));
        assertThat(filterparams.test(ytelse.withYtelse(BARNETRYGD)), is(true));
    }

    @Test
    public void skalToggleAlleYtelserSomUonsket() {
        Hovedytelse ytelse = new Hovedytelse()
                .withId(ID)
                .withHovedytelsedato(now())
                .withMottakertype(Mottakertype.BRUKER);

        filterparams.toggleAlleYtelser(false);
        assertThat(filterparams.test(ytelse.withYtelse(DAGPENGER)), is(false));
        assertThat(filterparams.test(ytelse.withYtelse(BARNETRYGD)), is(false));
    }

    @Test
    public void isAlleytelserValgt() {
        Set<String> ytelser = new HashSet<>(asList("Ytelse A", "Ytelse B"));
        FilterParametere params = new FilterParametere(ytelser);

        assertTrue(params.isAlleYtelserValgt());

        params.velgEnYtelse("Ytelse C");
        assertFalse(params.isAlleYtelserValgt());
    }

    @Test
    public void toggleAlleYtelser() {
        Set<String> ytelser = new HashSet<>(asList("Ytelse A", "Ytelse B"));
        FilterParametere params = new FilterParametere(ytelser);

        params.toggleAlleYtelser(true);
        assertTrue(params.isAlleYtelserValgt());

        params.toggleAlleYtelser(false);
        assertFalse(params.isAlleYtelserValgt());
    }

    @Test
    public void intervallHvisSiste3Dager() {
        Interval interval = filterparams.intervalBasertPaaPeriodevalg(PeriodeVelger.SISTE_30_DAGER);
        assertThat(LocalDate.now().minusDays(30), is(interval.getStart().toLocalDate()));
        assertThat(LocalDate.now().plusDays(ANTALL_DAGER_FRAMOVER_I_TID), is(interval.getEnd().toLocalDate()));
    }

    @Test
    public void intervalHvisInnevaerendeAar() {
        Interval interval = filterparams.intervalBasertPaaPeriodevalg(PeriodeVelger.INNEVAERENDE_AAR);
        assertThat(new LocalDate(now().getYear(), 1, 1), is(interval.getStart().toLocalDate()));
        assertThat(new LocalDate(now().getYear(), 31, 12), is(interval.getEnd().toLocalDate()));
    }

    @Test
    public void intervalHvisIfjor() {
        Interval interval = filterparams.intervalBasertPaaPeriodevalg(PeriodeVelger.I_FJOR);
        assertThat(new LocalDate(now().getYear()-1, 1, 1), is(interval.getStart().toLocalDate()));
        assertThat(new LocalDate(now().getYear()-1, 12, 31), is(interval.getEnd().toLocalDate()));
    }

    @Test
    public void intervalHvisEgendefinert() {
        filterparams.setStartDato(new LocalDate(2013, 1, 2));
        filterparams.setSluttDato(new LocalDate(2013, 12, 31));

        Interval interval = filterparams.intervalBasertPaaPeriodevalg(PeriodeVelger.EGENDEFINERT);
        assertThat(new LocalDate(2013, 1, 2), is(interval.getStart().toLocalDate()));
        assertThat(new LocalDate(2013, 12, 31), is(interval.getEnd().toLocalDate()));
    }
}

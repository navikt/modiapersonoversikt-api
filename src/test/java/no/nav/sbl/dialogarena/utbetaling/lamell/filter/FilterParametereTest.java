package no.nav.sbl.dialogarena.utbetaling.lamell.filter;


import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.Mottaktertype;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.getBuilder;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class FilterParametereTest {

    private static final String DAGPENGER = "Dagpenger";
    private static final String BARNETRYGD = "Barnetrygd";

    private FilterParametere filterparams;

    @Before
    public void settOppFilter() {
        filterparams = new FilterParametere(new HashSet<>(asList(DAGPENGER, BARNETRYGD)));
    }

    @Test
    public void filtrererBortUtbetalingUtenforDatointervall() {
        Utbetaling utbetaling = getBuilder()
                .withUtbetalingsDato(now().minusYears(2))
                .withMottakertype(Mottaktertype.BRUKER)
                .withHovedytelse(DAGPENGER)
                .createUtbetaling();

        assertFalse(filterparams.evaluate(utbetaling));
    }

    @Test
    public void filtrererBortUtbetalingForAnnenMottakertype() {
        Utbetaling utbetaling = getBuilder()
                .withUtbetalingsDato(now())
                .withMottakertype(Mottaktertype.ANNEN_MOTTAKER)
                .withHovedytelse(DAGPENGER)
                .createUtbetaling();

        filterparams.toggleMottaker(Mottaktertype.ANNEN_MOTTAKER);

        assertFalse(filterparams.evaluate(utbetaling));
    }

    @Test
    public void skalBeholdeYtelsenHvisUtbetalingenInneholderEnYtelseManVilHa() {
        Utbetaling utbetaling = getBuilder()
                .withUtbetalingsDato(now())
                .withMottakertype(Mottaktertype.BRUKER)
                .withHovedytelse(DAGPENGER)
                .createUtbetaling();

        filterparams.leggTilOnsketYtelse(BARNETRYGD);

        assertTrue(filterparams.evaluate(utbetaling));
    }

    @Test
    public void skalIkkeBeholdeYtelsenHvisAlleUtbetalingerErUonskede() {
        Utbetaling utbetaling = getBuilder()
                .withUtbetalingsDato(now())
                .withMottakertype(Mottaktertype.BRUKER)
                .withHovedytelse(DAGPENGER)
                .createUtbetaling();

        filterparams.velgEnYtelse(BARNETRYGD);

        assertFalse(filterparams.evaluate(utbetaling));
    }

    @Test
    public void skalViseAlleUtbetalingerHvisAlleYtelserErValgtOgNyeYtelserBlirSatt() {
        filterparams = new FilterParametere(new HashSet<String>());
        Utbetaling utbetaling = getBuilder()
                .withUtbetalingsDato(now())
                .withMottakertype(Mottaktertype.BRUKER)
                .withHovedytelse(DAGPENGER)
                .createUtbetaling();
        filterparams.setYtelser(new HashSet<>(asList(utbetaling.getHovedytelse())));

        assertThat(filterparams.evaluate(utbetaling), is(true));
    }

    @Test
    public void skalToggleVerdiBasertPaaMottaker() {
        assertThat(filterparams.viseMottaker(Mottaktertype.BRUKER), is(true));
        filterparams.toggleMottaker(Mottaktertype.BRUKER);
        assertThat(filterparams.viseMottaker(Mottaktertype.BRUKER), is(false));
        filterparams.toggleMottaker(Mottaktertype.BRUKER);
        assertThat(filterparams.viseMottaker(Mottaktertype.BRUKER), is(true));
    }
}

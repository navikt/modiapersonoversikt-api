package no.nav.sbl.dialogarena.utbetaling.lamell.filter;


import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.ARBEIDSGIVER;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.BRUKER;
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
        HashMap<String, Boolean> mottakere = new HashMap<>();
        mottakere.put(ARBEIDSGIVER, true);
        mottakere.put(BRUKER, true);

        filterparams = new FilterParametere(new HashSet<>(asList(DAGPENGER, BARNETRYGD)));
    }

    @Test
    public void filtrererBortUtbetalingUtenforDatointervall() {
        Utbetaling utbetaling = getBuilder()
                .withUtbetalingsDato(now().minusYears(2))
                .withMottakerkode(BRUKER)
                .withHovedytelse(DAGPENGER)
                .createUtbetaling();

        assertFalse(filterparams.evaluate(utbetaling));
    }

    @Test
    public void filtrererBortUtbetalingForAnnenMottakertype() {
        Utbetaling utbetaling = getBuilder()
                .withUtbetalingsDato(now())
                .withMottakerkode(ARBEIDSGIVER)
                .withHovedytelse(DAGPENGER)
                .createUtbetaling();

        filterparams.toggleMottaker(ARBEIDSGIVER);

        assertFalse(filterparams.evaluate(utbetaling));
    }

    @Test
    public void skalBeholdeYtelsenHvisUtbetalingenInneholderEnYtelseManVilHa() {
        Utbetaling utbetaling = getBuilder()
                .withUtbetalingsDato(now())
                .withMottakerkode(BRUKER)
                .withHovedytelse(DAGPENGER)
                .createUtbetaling();

        filterparams.leggTilOnsketYtelse(BARNETRYGD);

        assertTrue(filterparams.evaluate(utbetaling));
    }

    @Test
    public void skalIkkeBeholdeYtelsenHvisAlleUtbetalingerErUonskede() {
        Utbetaling utbetaling = getBuilder()
                .withUtbetalingsDato(now())
                .withMottakerkode(BRUKER)
                .withHovedytelse(DAGPENGER)
                .createUtbetaling();

        filterparams.velgEnYtelse(BARNETRYGD);

        assertFalse(filterparams.evaluate(utbetaling));
    }

    @Test
    public void skalToggleVerdiBasertPaaMottaker() {
        assertThat(filterparams.viseMottaker(BRUKER), is(true));
        filterparams.toggleMottaker(BRUKER);
        assertThat(filterparams.viseMottaker(BRUKER), is(false));
        filterparams.toggleMottaker(BRUKER);
        assertThat(filterparams.viseMottaker(BRUKER), is(true));
    }

    @Test
    public void skalIkkeViseMottakereSomIkkeFinnes() {
        assertThat(filterparams.viseMottaker("someRandomMottaker"), is(false));
    }
}

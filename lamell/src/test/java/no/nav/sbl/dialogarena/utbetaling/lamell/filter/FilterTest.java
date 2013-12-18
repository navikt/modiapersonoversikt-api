package no.nav.sbl.dialogarena.utbetaling.lamell.filter;


import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.ARBEIDSGIVER;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.BRUKER;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.getBuilder;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FilterTest {

    private static final String DAGPENGER = "Dagpenger";
    private static final String BARNETRYGD = "Barnetrygd";

    private FilterParametere filterparams;

    @Before
    public void settOppFilter() {
        filterparams = new FilterParametere(LocalDate.now().minusYears(1), LocalDate.now(), true, true,
                new HashSet<>(asList(DAGPENGER, BARNETRYGD)));
    }

    @Test
    public void filtrererBortUtbetalingUtenforDatointervall() {
        Utbetaling utbetaling = getBuilder()
                .withUtbetalingsDato(now().minusYears(2))
                .withMottakerId(BRUKER)
                .withHovedytelse(DAGPENGER)
                .createUtbetaling();

        assertFalse(filterparams.evaluate(utbetaling));
    }

    @Test
    public void filtrererBortUtbetalingForAnnenMottakertype() {
        Utbetaling utbetaling = getBuilder()
                .withUtbetalingsDato(now())
                .withMottakerId(ARBEIDSGIVER)
                .withHovedytelse(DAGPENGER)
                .createUtbetaling();

        filterparams.visArbeidsgiver = false;

        assertFalse(filterparams.evaluate(utbetaling));
    }

    @Test
    public void skalBeholdeYtelsenHvisUtbetalingenInneholderEnYtelseManVilHa() {
        Utbetaling utbetaling = getBuilder()
                .withUtbetalingsDato(now())
                .withMottakerId(BRUKER)
                .withHovedytelse(DAGPENGER)
                .createUtbetaling();

        filterparams.uonskedeYtelser.add(BARNETRYGD);

        assertTrue(filterparams.evaluate(utbetaling));
    }

    @Test
    public void skalIkkeBeholdeYtelsenHvisAlleUtbetalingerErUonskede() {
        Utbetaling utbetaling = getBuilder()
                .withUtbetalingsDato(now())
                .withMottakerId(BRUKER)
                .withHovedytelse(DAGPENGER)
                .createUtbetaling();

        filterparams.uonskedeYtelser.add(DAGPENGER);
        filterparams.uonskedeYtelser.add(BARNETRYGD);

        assertFalse(filterparams.evaluate(utbetaling));
    }
}

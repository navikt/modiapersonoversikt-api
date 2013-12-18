package no.nav.sbl.dialogarena.utbetaling.lamell.filter;


import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.HashSet;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.getBuilder;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FilterTest {

    private static final String DAGPENGER = "Dagpenger";
    private static final String BARNETRYGD = "Barnetrygd";
    private static Utbetaling utbetaling;

    static {

        utbetaling = getBuilder()
                .withUtbetalingsDato(now())
                .withPeriode(new Interval(now().minusDays(1), now()))
                .withMottakerId("bruker")
                .createUtbetaling();
    }

    @Test
    public void skalBeholdeYtelserSomErOnskede() {
        FilterParametere filterparams = new FilterParametere(LocalDate.now().minusYears(1), LocalDate.now(), true, true,
                new HashSet<>(asList(DAGPENGER, BARNETRYGD)));
        assertTrue(filterparams.evaluate(utbetaling));

    }

    @Test
    public void skalBeholdeYtelsenHvisUtbetalingenInneholderEnYtelseManVilHa() {
        FilterParametere filterparams = new FilterParametere(LocalDate.now().minusYears(1), LocalDate.now(), true, true,
                new HashSet<>(asList(DAGPENGER, BARNETRYGD)));
        filterparams.uonskedeYtelser.add(DAGPENGER);
        assertTrue(filterparams.evaluate(utbetaling));
    }

    @Test
    public void skalIkkeBeholdeYtelsenHvisAlleUtbetalingerErUonskede() {
        FilterParametere filterparams = new FilterParametere(LocalDate.now().minusYears(1), LocalDate.now(), true, true,
                new HashSet<>(asList(DAGPENGER, BARNETRYGD)));
        filterparams.uonskedeYtelser.add(DAGPENGER);
        filterparams.uonskedeYtelser.add(BARNETRYGD);
        assertFalse(filterparams.evaluate(utbetaling));
    }
}

package no.nav.sbl.dialogarena.utbetaling.lamell.filter;


import no.nav.sbl.dialogarena.utbetaling.domain.Bilag;
import no.nav.sbl.dialogarena.utbetaling.domain.PosteringsDetalj;
import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.HashSet;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FilterTest {

    private static final String DAGPENGER = "Dagpenger";
    private static final String BARNETRYGD = "Barnetrygd";
    private static Utbetaling utbetaling;

    static {
        PosteringsDetalj dagpenger = new PosteringsDetalj();
        dagpenger.hovedBeskrivelse = DAGPENGER;
        PosteringsDetalj barnetrygd = new PosteringsDetalj();
        barnetrygd.hovedBeskrivelse = BARNETRYGD;
        Bilag bilag = new Bilag();
        bilag.posteringsDetaljer = asList(dagpenger, barnetrygd);
        utbetaling = new UtbetalingBuilder().setUtbetalingsDato(UUID.randomUUID().toString()).createUtbetaling();
        utbetaling.startDato = DateTime.now().minusDays(1);
        utbetaling.utbetalingsDato = DateTime.now();
        utbetaling.mottakertype = "bruker";
        utbetaling.bilag = asList(bilag);
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

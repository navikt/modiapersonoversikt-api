package no.nav.sbl.dialogarena.utbetaling.domain;

import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.sort;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.UtbetalingBuilder;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.UtbetalingComparator.UTBETALING_DAG_YTELSE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class UtbetalingTest {

    @Test
    public void testSortering_SammeDato_SorterPaaNavn() throws Exception {
        DateTime idag = new DateTime();
        Utbetaling utbetaling = new UtbetalingBuilder().withHovedytelse("Uføre").withUtbetalingsDato(idag).createUtbetaling();
        Utbetaling utbetaling1 = new UtbetalingBuilder().withHovedytelse("Foreldrepenger").withUtbetalingsDato(idag).createUtbetaling();
        List<Utbetaling> utbetalinger = asList(utbetaling, utbetaling1);

        sort(utbetalinger, UTBETALING_DAG_YTELSE);

        assertThat(utbetalinger.get(0).getHovedytelse(), is("Foreldrepenger"));
        assertThat(utbetalinger.get(1).getHovedytelse(), is("Uføre"));
    }

    @Test
    public void testSortering_ForskjelligDato_SorterNyestForst() throws Exception {
        DateTime idag = new DateTime();
        DateTime igaar = idag.minusDays(1);
        Utbetaling utbetaling = new UtbetalingBuilder().withHovedytelse("Uføre").withUtbetalingsDato(idag).createUtbetaling();
        Utbetaling utbetaling1 = new UtbetalingBuilder().withHovedytelse("Foreldrepenger").withUtbetalingsDato(igaar).createUtbetaling();
        List<Utbetaling> utbetalinger = asList(utbetaling, utbetaling1);

        sort(utbetalinger, UTBETALING_DAG_YTELSE);

        assertThat(utbetalinger.get(0).getHovedytelse(), is("Uføre"));
        assertThat(utbetalinger.get(1).getHovedytelse(), is("Foreldrepenger"));
    }
}

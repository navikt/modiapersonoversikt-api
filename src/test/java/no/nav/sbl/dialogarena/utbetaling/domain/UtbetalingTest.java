package no.nav.sbl.dialogarena.utbetaling.domain;

import org.joda.time.DateTime;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.sort;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.UtbetalingBuilder;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.UtbetalingComparator.UTBETALING_DAG_YTELSE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class UtbetalingTest {

    private static final String ID = "id";

    @Test
    public void testSortering_SammeDato_SorterPaaNavn() throws Exception {
        DateTime idag = new DateTime();
        Utbetaling utbetaling = new UtbetalingBuilder(ID).withHovedytelse("Uføre").withUtbetalingsDato(idag).build();
        Utbetaling utbetaling1 = new UtbetalingBuilder(ID).withHovedytelse("Foreldrepenger").withUtbetalingsDato(idag).build();
        List<Utbetaling> utbetalinger = asList(utbetaling, utbetaling1);

        sort(utbetalinger, UTBETALING_DAG_YTELSE);

        assertThat(utbetalinger.get(0).getHovedytelse(), is("Foreldrepenger"));
        assertThat(utbetalinger.get(1).getHovedytelse(), is("Uføre"));
    }

    @Test
    public void testSortering_ForskjelligDato_SorterNyestForst() throws Exception {
        DateTime idag = new DateTime();
        DateTime igaar = idag.minusDays(1);
        Utbetaling utbetaling = new UtbetalingBuilder(ID).withHovedytelse("Uføre").withUtbetalingsDato(idag).build();
        Utbetaling utbetaling1 = new UtbetalingBuilder(ID).withHovedytelse("Foreldrepenger").withUtbetalingsDato(igaar).build();
        List<Utbetaling> utbetalinger = asList(utbetaling, utbetaling1);

        sort(utbetalinger, UTBETALING_DAG_YTELSE);

        assertThat(utbetalinger.get(0).getHovedytelse(), is("Uføre"));
        assertThat(utbetalinger.get(1).getHovedytelse(), is("Foreldrepenger"));
    }

    @Test
    public void testHashCodeOgEqualsTrue() {
        Utbetaling utbetaling1 = new UtbetalingBuilder(ID).build();
        Utbetaling utbetaling2 = new UtbetalingBuilder(ID).build();
        Set<Utbetaling> utbetalinger = new HashSet<>(asList(utbetaling1, utbetaling2));

        assertTrue(utbetaling1.equals(utbetaling2));
        assertEquals(1, utbetalinger.size());
    }

    @Test
    public void testHashCodeOgEqualsFalse() {
        Utbetaling utbetaling1 = new UtbetalingBuilder(ID).build();
        Utbetaling utbetaling2 = new UtbetalingBuilder(ID + "noeannet").build();
        Set<Utbetaling> utbetalinger = new HashSet<>(asList(utbetaling1, utbetaling2));

        assertFalse(utbetaling1.equals(utbetaling2));
        assertEquals(2, utbetalinger.size());
    }
}

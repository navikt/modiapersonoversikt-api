package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.sbl.dialogarena.common.records.Record;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.UtbetalingComparator.POSTERINGSDATO_COMPARATOR;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class UtbetalingTest {

    private static final String ID = "id";

    @Test
    public void testSortering_SammeDato_SorterPaaNavn() throws Exception {
        DateTime idag = new DateTime();
        Record<Hovedytelse> hovedytelse = new Record<Hovedytelse>()
                .with(Hovedytelse.id, ID)
                .with(Hovedytelse.ytelse, "Uføre")
                .with(Hovedytelse.posteringsdato, idag);

        Record<Hovedytelse> hovedytelse2 = new Record<Hovedytelse>()
                .with(Hovedytelse.id, ID)
                .with(Hovedytelse.ytelse, "Foreldrepenger")
                .with(Hovedytelse.posteringsdato, idag);

        List<Record<Hovedytelse>> hovedytelser = asList(hovedytelse, hovedytelse2);

        hovedytelser = on(hovedytelser).collect(POSTERINGSDATO_COMPARATOR);

        assertThat(hovedytelser.get(0).get(Hovedytelse.ytelse), is("Foreldrepenger"));
        assertThat(hovedytelser.get(1).get(Hovedytelse.ytelse), is("Uføre"));
    }

    @Test
    public void testSortering_ForskjelligDato_SorterNyestForst() throws Exception {
        DateTime idag = new DateTime();
        DateTime igaar = idag.minusDays(1);
        Record<Hovedytelse> hovedytelse = new Record<Hovedytelse>()
                .with(Hovedytelse.id, ID)
                .with(Hovedytelse.ytelse, "Uføre")
                .with(Hovedytelse.posteringsdato, idag);

        Record<Hovedytelse> hovedytelse2 = new Record<Hovedytelse>()
                .with(Hovedytelse.id, ID)
                .with(Hovedytelse.ytelse, "Foreldrepenger")
                .with(Hovedytelse.posteringsdato, igaar);

        List<Record<Hovedytelse>> hovedytelser = asList(hovedytelse, hovedytelse2);
        hovedytelser = on(hovedytelser).collect(POSTERINGSDATO_COMPARATOR);

        assertThat(hovedytelser.get(0).get(Hovedytelse.ytelse), is("Uføre"));
        assertThat(hovedytelser.get(1).get(Hovedytelse.ytelse), is("Foreldrepenger"));
    }

    @Test
    public void testHashCodeOgEqualsTrue() {
        Record<Hovedytelse> hovedytelse1 = new Record<Hovedytelse>().with(Hovedytelse.id, ID);
        Record<Hovedytelse> hovedytelse2 = new Record<Hovedytelse>().with(Hovedytelse.id, ID);
        Set<Record<Hovedytelse>> hovedytelser = new HashSet<>(asList(hovedytelse1, hovedytelse2));

        assertTrue(hovedytelse1.equals(hovedytelse2));
        assertEquals(1, hovedytelser.size());
    }

    @Test
    public void testHashCodeOgEqualsFalse() {
        Record<Hovedytelse> hovedytelse1 = new Record<Hovedytelse>().with(Hovedytelse.id, ID);
        Record<Hovedytelse> hovedytelse2 = new Record<Hovedytelse>().with(Hovedytelse.id, ID + "noeannet");
        Set<Record<Hovedytelse>> hovedytelser = new HashSet<>(asList(hovedytelse1, hovedytelse2));

        assertFalse(hovedytelse1.equals(hovedytelse2));
        assertEquals(2, hovedytelser.size());
    }
}

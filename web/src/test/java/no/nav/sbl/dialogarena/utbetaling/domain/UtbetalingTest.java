package no.nav.sbl.dialogarena.utbetaling.domain;

import org.joda.time.DateTime;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.utbetaling.domain.util.YtelseUtils.SISTE_HOVEDYTELSESDATO_FORST;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class UtbetalingTest {

    private static final String ID = "id";

    @Test
    public void testSortering_SammeDato_SorterPaaNavn() {
        DateTime idag = new DateTime();
        Hovedytelse hovedytelse = new Hovedytelse()
                .withId(ID)
                .withYtelse("Uføre")
                .withHovedytelsedato(idag);

        Hovedytelse hovedytelse2 = new Hovedytelse()
                .withId(ID)
                .withYtelse("Foreldrepenger")
                .withHovedytelsedato(idag);

        List<Hovedytelse> hovedytelser = asList(hovedytelse, hovedytelse2);

        hovedytelser = hovedytelser.stream().sorted(SISTE_HOVEDYTELSESDATO_FORST).collect(toList());

        assertThat(hovedytelser.get(0).getYtelse(), is("Foreldrepenger"));
        assertThat(hovedytelser.get(1).getYtelse(), is("Uføre"));
    }

    @Test
    public void testSortering_ForskjelligDato_SorterNyestForst() {
        DateTime idag = new DateTime();
        DateTime igaar = idag.minusDays(1);
        Hovedytelse hovedytelse = new Hovedytelse()
                .withId(ID)
                .withYtelse("Uføre")
                .withHovedytelsedato(idag);

        Hovedytelse hovedytelse2 = new Hovedytelse()
                .withId(ID)
                .withYtelse("Foreldrepenger")
                .withHovedytelsedato(igaar);

        List<Hovedytelse> hovedytelser = asList(hovedytelse, hovedytelse2);
        hovedytelser = hovedytelser.stream().sorted(SISTE_HOVEDYTELSESDATO_FORST).collect(toList());

        assertThat(hovedytelser.get(0).getYtelse(), is("Uføre"));
        assertThat(hovedytelser.get(1).getYtelse(), is("Foreldrepenger"));
    }


    @Test
    public void testHashCodeOgEqualsTrue() {
        Hovedytelse hovedytelse1 = new Hovedytelse().withId(ID);
        Hovedytelse hovedytelse2 = new Hovedytelse().withId(ID);
        Set<Hovedytelse> hovedytelser = new HashSet<>(asList(hovedytelse1, hovedytelse2));

        assertTrue(hovedytelse1.equals(hovedytelse2));
        assertEquals(1, hovedytelser.size());
    }

    @Test
    public void testHashCodeOgEqualsFalse() {
        Hovedytelse hovedytelse1 = new Hovedytelse().withId(ID);
        Hovedytelse hovedytelse2 = new Hovedytelse().withId(ID + "noeannet");
        Set<Hovedytelse> hovedytelser = new HashSet<>(asList(hovedytelse1, hovedytelse2));

        assertFalse(hovedytelse1.equals(hovedytelse2));
        assertEquals(2, hovedytelser.size());
    }
}

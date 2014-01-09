package no.nav.sbl.dialogarena.utbetaling.domain;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static no.nav.sbl.dialogarena.utbetaling.domain.Underytelse.UnderytelseBuilder;


public class UnderytelseTest {

    @Test
    public void testEqualsOgHashCode() throws Exception {
        Underytelse x = new UnderytelseBuilder().setTittel("Prosjekt").setAntall(1).setBelop(2000.0).createUnderytelse();
        Underytelse y = new UnderytelseBuilder().setTittel("Prosjekt").setAntall(1).setBelop(2000.0).createUnderytelse();
        assertTrue(x.equals(y) && y.equals(x));
        assertTrue(x.hashCode() == y.hashCode());
    }
}

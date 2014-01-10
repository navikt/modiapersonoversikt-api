package no.nav.sbl.dialogarena.utbetaling.domain;

import org.junit.Test;

import static no.nav.sbl.dialogarena.utbetaling.domain.Underytelse.UnderytelseBuilder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class UnderytelseTest {

    @Test
    public void testEqualsOgHashCode() throws Exception {
        Underytelse x = new UnderytelseBuilder().setTittel("Prosjekt").setAntall(1).setBelop(2000.0).createUnderytelse();
        Underytelse y = new UnderytelseBuilder().setTittel("Prosjekt").setAntall(1).setBelop(2000.0).createUnderytelse();
        assertThat(x.equals(y) && y.equals(x), is(true));
        assertThat(x.hashCode() == y.hashCode(), is(true));
    }
}

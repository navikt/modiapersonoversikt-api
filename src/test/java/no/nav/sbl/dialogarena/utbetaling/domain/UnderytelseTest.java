package no.nav.sbl.dialogarena.utbetaling.domain;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class UnderytelseTest {

    @Test
    public void testEqualsOgHashCode() throws Exception {
        Underytelse x = new Underytelse("Prosjekt", "", 1, 2000.0, 0.0);
        Underytelse y = new Underytelse("Prosjekt", "", 1, 2000.0, 0.0);
        assertThat(x.equals(y) && y.equals(x), is(true));
        assertThat(x.hashCode() == y.hashCode(), is(true));
    }
}

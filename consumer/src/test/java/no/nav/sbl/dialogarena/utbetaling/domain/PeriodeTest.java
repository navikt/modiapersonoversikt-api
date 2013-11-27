package no.nav.sbl.dialogarena.utbetaling.domain;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


public class PeriodeTest {
    @Test
    public void testContainsDate() throws Exception {
        DateTime dato0 = DateTime.now().minusDays(16);
        DateTime dato1 = DateTime.now().minusDays(15);
        DateTime dato2 = DateTime.now().minusDays(10);
        DateTime dato3 = DateTime.now().minusDays(5);
        DateTime dato4 = DateTime.now().minusDays(1);
        DateTime dato5 = DateTime.now();
        Periode periode = new Periode(dato1, dato4);

        assertThat(periode.containsDate(dato0), is(false));
        assertThat(periode.containsDate(dato1), is(true));
        assertThat(periode.containsDate(dato2), is(true));
        assertThat(periode.containsDate(dato3), is(true));
        assertThat(periode.containsDate(dato4), is(true));
        assertThat(periode.containsDate(dato5), is(false));
    }
}

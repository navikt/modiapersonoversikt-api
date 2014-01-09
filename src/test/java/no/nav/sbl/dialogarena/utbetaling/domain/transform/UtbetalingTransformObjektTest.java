package no.nav.sbl.dialogarena.utbetaling.domain.transform;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;

public class UtbetalingTransformObjektTest {

    @Test
    public void testEqualsOgHashCode() throws Exception {
        DateTime dato = DateTime.now().minusDays(1);
        UtbetalingTransformObjekt x = new UtbetalingTransformObjekt.UtbetalingTransformObjektBuilder().withHovedYtelse("Papp").withSpesifikasjon("Ekstra info").withPeriode(new Interval(2000L, 4000L)).withMottakerId("346534").withKontonummer("12837612 67").withBelop(2000.0).withUtbetalingsDato(dato).build();
        UtbetalingTransformObjekt y = new UtbetalingTransformObjekt.UtbetalingTransformObjektBuilder().withHovedYtelse("Papp").withSpesifikasjon("Ekstra info").withPeriode(new Interval(2000L, 4000L)).withMottakerId("346534").withKontonummer("12837612 67").withBelop(2000.0).withUtbetalingsDato(dato).build();

        assertTrue(x.equals(y) && y.equals(x));
        assertTrue(x.hashCode() == y.hashCode());
    }
}

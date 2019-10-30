package no.nav.sykmeldingsperioder.domain.sykepenger;

import no.nav.kjerneinfo.common.domain.Periode;
import org.joda.time.LocalDate;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ForsikringTest {

    public static final String FORSIKRINGSORDNING = "fORSIKRINGS ORDNING";
    public static final Double PREMIEGRUNNLAG = new Double(2.5);
    public static final boolean GYLDIG = true;
    public static final LocalDate PERIODEFRA = new LocalDate(2013, 1, 1);
    public static final LocalDate PERIODETOM = new LocalDate(2014, 1, 1);
    public static final Periode PERIODE = new Periode(PERIODEFRA, PERIODETOM);

    @Test
    public void testBean() {
        Forsikring forsikring = new Forsikring();
        forsikring.setForsikringsordning(FORSIKRINGSORDNING);
        forsikring.setPremiegrunnlag(PREMIEGRUNNLAG);
        forsikring.setErGyldig(GYLDIG);
        forsikring.setForsikret(PERIODE);

        assertEquals(FORSIKRINGSORDNING, forsikring.getForsikringsordning());
        assertEquals(PREMIEGRUNNLAG, forsikring.getPremiegrunnlag());
        assertEquals(GYLDIG, forsikring.getErGyldig());
        assertEquals(PERIODE, forsikring.getForsikret());

        Forsikring forsikring2 = new Forsikring(GYLDIG, FORSIKRINGSORDNING, PERIODE, PREMIEGRUNNLAG);

        assertEquals(FORSIKRINGSORDNING, forsikring2.getForsikringsordning());
        assertEquals(PREMIEGRUNNLAG, forsikring2.getPremiegrunnlag());
        assertEquals(GYLDIG, forsikring2.getErGyldig());
        assertEquals(PERIODE, forsikring2.getForsikret());
    }
}

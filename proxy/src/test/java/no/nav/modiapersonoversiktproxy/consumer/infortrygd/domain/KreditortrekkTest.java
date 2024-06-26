package no.nav.modiapersonoversiktproxy.consumer.infortrygd.domain;

import no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.Kreditortrekk;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class KreditortrekkTest {

    public static final String KREDITORSNAVN = "Kreditor";
    public static final String KREDITORSNAVN_2 = "KreditorNr2";
    public static final Double BELOP = 256.22;
    private Kreditortrekk kreditortrekk;
    private Kreditortrekk kreditortrekk2;

    @Before
    public void setUp() {
        kreditortrekk = new Kreditortrekk();
        kreditortrekk2 = new Kreditortrekk();
    }

    @Test
    public void testBean() {
        kreditortrekk.setKreditorsNavn(KREDITORSNAVN);
        kreditortrekk.setBelop(BELOP);
        assertEquals(KREDITORSNAVN, kreditortrekk.getKreditorsNavn());
        assertEquals(BELOP, kreditortrekk.getBelop());
    }

    @Test
    public void testEquals() {
        kreditortrekk.setKreditorsNavn(KREDITORSNAVN);
        kreditortrekk.setBelop(BELOP);
        kreditortrekk2.setKreditorsNavn(KREDITORSNAVN_2);
        kreditortrekk2.setBelop(BELOP);
        Object emptyObject = new Object();

        assertNotEquals(kreditortrekk, kreditortrekk2);
        assertNotEquals(null, kreditortrekk);
        kreditortrekk2.setKreditorsNavn(KREDITORSNAVN);
        assert(kreditortrekk.equals(kreditortrekk2));
        kreditortrekk2.setBelop(BELOP-1);
        assertNotEquals(kreditortrekk, kreditortrekk2);
        assertNotEquals(kreditortrekk, emptyObject);
    }
}

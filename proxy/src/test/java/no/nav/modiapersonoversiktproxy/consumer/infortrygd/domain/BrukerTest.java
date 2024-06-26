package no.nav.modiapersonoversiktproxy.consumer.infortrygd.domain;

import no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.Bruker;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class BrukerTest {
    public static final String PERSONID = "ABC123";

    @Test
    public void testBean() {
        Bruker bruker = new Bruker();
        bruker.setIdent(PERSONID);
        assertEquals(PERSONID, bruker.getIdent());
    }
}

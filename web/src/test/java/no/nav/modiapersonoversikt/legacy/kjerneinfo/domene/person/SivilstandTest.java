package no.nav.modiapersonoversikt.legacy.kjerneinfo.domene.person;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain.Kodeverdi;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SivilstandTest {

    @Test
    public void testSivilstand() {
        Kodeverdi sivilstand = new Kodeverdi.With().kodeRef("GIFT").done();

        assertNotNull(sivilstand.getKodeRef());
        assertEquals("GIFT", sivilstand.getKodeRef());
    }
}

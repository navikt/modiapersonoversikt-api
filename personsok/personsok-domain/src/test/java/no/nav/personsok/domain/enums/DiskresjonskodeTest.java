package no.nav.personsok.domain.enums;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class DiskresjonskodeTest {

    @Test
    public void testDiskresjonsKode() {
        assertEquals(Diskresjonskode.KODE_1, Diskresjonskode.withKode("1"));
        assertEquals("Egen ansatt eller ansattes familie", Diskresjonskode.withKode("5").getBeskrivelse());
        assertEquals(Diskresjonskode.KODE_5, Diskresjonskode.withKode(Diskresjonskode.KODE_5.getKode()));
        assertNull(Diskresjonskode.withKode("99991"));
    }
}

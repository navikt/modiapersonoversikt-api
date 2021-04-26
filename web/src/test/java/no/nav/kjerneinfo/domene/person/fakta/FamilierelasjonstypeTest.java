package no.nav.kjerneinfo.domene.person.fakta;

import no.nav.kjerneinfo.domain.person.fakta.Familierelasjonstype;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FamilierelasjonstypeTest {

    @Test
    public void testFamilierelasjonstypeFromValue() {
        assertEquals(Familierelasjonstype.SAMBOER,
                Familierelasjonstype.fromValue("samboer"));

        for (Familierelasjonstype relasjonstype : Familierelasjonstype.values()) {
            assertEquals(relasjonstype, Familierelasjonstype.fromValue(relasjonstype.value()));
        }

        try {
            Familierelasjonstype.fromValue("IkkeEksistrendeNavnType");
            fail();
        } catch (IllegalArgumentException e) {
        }
    }
}

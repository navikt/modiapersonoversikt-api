package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.knyttbehandlingskjedetilsak;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KnyttBehandlingskjedeTilSakValidatorTest {

    @Test
    void utenEnhetKasterFeil() {
        assertThrows(EnhetIkkeSatt.class, () ->
                KnyttBehandlingskjedeTilSakValidator.validate("10108000398", "behandlingskjede", new Sak(), ""));
    }

    @Test
    void getSaksIdSakErNull(){
        assertThat(KnyttBehandlingskjedeTilSakValidator.getSaksId(null), is("INGEN SAKSID"));
    }

    @Test
    void getSaksIdSaksIdErNull(){
        Sak sak = new Sak();
        sak.saksId = null;
        assertThat(KnyttBehandlingskjedeTilSakValidator.getSaksId(sak), is("INGEN SAKSID"));
    }

    @Test
    void getSaksIdGirSaksId(){
        Sak sak = new Sak();
        sak.saksId = "saksid";
        assertThat(KnyttBehandlingskjedeTilSakValidator.getSaksId(sak), is("saksid"));
    }

}
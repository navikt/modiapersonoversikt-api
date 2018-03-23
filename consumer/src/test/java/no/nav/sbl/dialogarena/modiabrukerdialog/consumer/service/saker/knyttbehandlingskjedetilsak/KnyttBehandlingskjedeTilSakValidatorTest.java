package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.knyttbehandlingskjedetilsak;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KnyttBehandlingskjedeTilSakValidatorTest {

    @Test
    void utenEnhetKasterFeil() {
        assertThrows(EnhetIkkeSatt.class, () ->
                KnyttBehandlingskjedeTilSakValidator.validate("10108000398", "behandlingskjede", new Sak(), ""));
    }

}
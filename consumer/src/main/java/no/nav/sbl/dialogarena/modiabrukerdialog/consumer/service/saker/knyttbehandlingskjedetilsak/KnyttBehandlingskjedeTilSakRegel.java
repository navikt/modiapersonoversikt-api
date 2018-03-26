package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.knyttbehandlingskjedetilsak;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;

public interface KnyttBehandlingskjedeTilSakRegel {

    void validate(String fnr, String behandlingskjede, Sak sak, String enhet);

}

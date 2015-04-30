package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Sak;

import java.util.List;

public interface HenvendelseUtsendingService {
    void sendHenvendelse(Melding melding, Optional<String> oppgaveId, Optional<Sak> sak) throws Exception;

    List<Melding> hentTraad(String fnr, String traadId);

    class OppgaveErFerdigstilt extends Exception {
    }
}

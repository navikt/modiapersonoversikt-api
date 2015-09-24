package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;

import java.util.List;

public interface HenvendelseUtsendingService {
    void sendHenvendelse(Melding melding, Optional<String> oppgaveId, Optional<Sak> sak) throws Exception;

    String opprettHenvendelse(String type, String fnr, String behandlingskjedeId);

    void ferdigstillHenvendelse (Melding melding, Optional<String> oppgaveId, Optional<Sak> sak, String behandlingsId) throws Exception;

    void avbrytHenvendelse(String behandlingsId);

    List<Melding> hentTraad(String fnr, String traadId);

    void merkSomKontorsperret(String fnr, List<String> meldingsIDer);

    void oppdaterTemagruppe(String behandlingsId, String temagruppe);

    class OppgaveErFerdigstilt extends Exception {
    }
}

package no.nav.sbl.dialogarena.modiabrukerdialog.api.service;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;

import java.util.List;
import java.util.Optional;

public interface HenvendelseUtsendingService {
    void sendHenvendelse(Melding melding, Optional<String> oppgaveId, Optional<Sak> sak, String saksbehandlersValgteEnhet) throws Exception;

    String opprettHenvendelse(String type, String fnr, String behandlingskjedeId);

    void ferdigstillHenvendelse (Melding melding, Optional<String> oppgaveId, Optional<Sak> sak, String behandlingsId, String saksbehandlersValgteEnhet) throws Exception;

    void avbrytHenvendelse(String behandlingsId);

    List<Melding> hentTraad(String fnr, String traadId, String valgtEnhet);

    void merkSomKontorsperret(String fnr, List<String> meldingsIDer);

    void oppdaterTemagruppe(String behandlingsId, String temagruppe);

    String slaaSammenTraader(List<String> traadIder);

    class OppgaveErFerdigstilt extends Exception {
    }
}

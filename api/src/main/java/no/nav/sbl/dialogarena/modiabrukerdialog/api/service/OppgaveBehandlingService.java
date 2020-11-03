package no.nav.sbl.dialogarena.modiabrukerdialog.api.service;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;

import java.util.List;
import java.util.Optional;

public interface OppgaveBehandlingService {

    void tilordneOppgaveIGsak(String oppgaveId, Temagruppe temagruppe, String saksbehandlersValgteEnhet) throws FikkIkkeTilordnet;

    List<Oppgave> finnTildelteOppgaverIGsak();

    List<Oppgave> plukkOppgaverFraGsak(Temagruppe temagruppe, String saksbehandlersValgteEnhet);

    void ferdigstillOppgaveIGsak(String oppgaveId, Optional<Temagruppe> temagruppe, String saksbehandlersValgteEnhet);

    void ferdigstillOppgaveIGsak(String oppgaveId, Temagruppe temagruppe, String saksbehandlersValgteEnhet);

    void ferdigstillOppgaverIGsak(List<String> oppgaveId, Optional<Temagruppe> temagruppe, String saksbehandlersValgteEnhet);

    void ferdigstillOppgaveIGsak(String oppgaveId, Optional<Temagruppe> temagruppe, String saksbehandlersValgteEnhet, String beskrivelse);

    void leggTilbakeOppgaveIGsak(LeggTilbakeOppgaveIGsakRequest request);

    void leggTilbakeOppgaveIGsak(List<LeggTilbakeOppgaveIGsakRequest> request);

    void systemLeggTilbakeOppgaveIGsak(String oppgaveId, Temagruppe temagruppe, String saksbehandlersValgteEnhet);

    boolean oppgaveErFerdigstilt(String oppgaveid);

    class FikkIkkeTilordnet extends Exception {
        public FikkIkkeTilordnet(Throwable cause) {
            super(cause);
        }
    }
}

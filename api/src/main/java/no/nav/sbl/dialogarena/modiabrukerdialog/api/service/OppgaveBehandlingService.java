package no.nav.sbl.dialogarena.modiabrukerdialog.api.service;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;

import java.util.List;
import java.util.Optional;

public interface OppgaveBehandlingService {
    Oppgave hentOppgave(String oppgaveId);

    OpprettOppgaveResponse opprettOppgave(OpprettOppgaveRequest request);

    OpprettOppgaveResponse opprettSkjermetOppgave(OpprettSkjermetOppgaveRequest request);

    void tilordneOppgaveIGsak(String oppgaveId, Temagruppe temagruppe, String saksbehandlersValgteEnhet) throws FikkIkkeTilordnet;

    @Deprecated
    List<Oppgave> finnTildelteOppgaverIGsak();
    List<Oppgave> finnTildelteOppgaverIGsak(String fnr);
    List<Oppgave> finnTildelteKNAOppgaverIGsak();

    List<Oppgave> plukkOppgaverFraGsak(Temagruppe temagruppe, String saksbehandlersValgteEnhet);

    void ferdigstillOppgaveIGsak(String oppgaveId, Optional<Temagruppe> temagruppe, String saksbehandlersValgteEnhet);

    void ferdigstillOppgaveIGsak(String oppgaveId, Temagruppe temagruppe, String saksbehandlersValgteEnhet);

    void ferdigstillOppgaverIGsak(List<String> oppgaveId, Optional<Temagruppe> temagruppe, String saksbehandlersValgteEnhet);

    void ferdigstillOppgaveIGsak(String oppgaveId, Optional<Temagruppe> temagruppe, String saksbehandlersValgteEnhet, String beskrivelse);

    void leggTilbakeOppgaveIGsak(LeggTilbakeOppgaveIGsakRequest request);

    void systemLeggTilbakeOppgaveIGsak(String oppgaveId, Temagruppe temagruppe, String saksbehandlersValgteEnhet);

    boolean oppgaveErFerdigstilt(String oppgaveid);

    class FikkIkkeTilordnet extends Exception {
        public FikkIkkeTilordnet(Throwable cause) {
            super(cause);
        }
    }
}

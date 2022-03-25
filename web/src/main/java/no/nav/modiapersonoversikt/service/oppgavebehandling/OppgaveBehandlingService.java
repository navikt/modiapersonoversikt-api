package no.nav.modiapersonoversikt.service.oppgavebehandling;

import no.nav.modiapersonoversikt.legacy.api.domain.Temagruppe;

import java.util.List;
import java.util.Optional;

public interface OppgaveBehandlingService {
    Oppgave hentOppgave(String oppgaveId);

    OpprettOppgaveResponse opprettOppgave(OpprettOppgaveRequest request);

    OpprettOppgaveResponse opprettSkjermetOppgave(OpprettSkjermetOppgaveRequest request);

    void tilordneOppgaveIGsak(String oppgaveId, Temagruppe temagruppe, String saksbehandlersValgteEnhet, boolean tvungenTilordning) throws FikkIkkeTilordnet;

    @Deprecated
    List<Oppgave> finnTildelteOppgaverIGsak();
    List<Oppgave> finnTildelteOppgaverIGsak(String fnr);

    void ferdigstillOppgaveIGsak(String oppgaveId, Optional<Temagruppe> temagruppe, String saksbehandlersValgteEnhet);

    void ferdigstillOppgaveIGsak(String oppgaveId, Temagruppe temagruppe, String saksbehandlersValgteEnhet);

    void ferdigstillOppgaverIGsak(List<String> oppgaveId, Optional<Temagruppe> temagruppe, String saksbehandlersValgteEnhet);

    void ferdigstillOppgaveIGsak(String oppgaveId, Optional<Temagruppe> temagruppe, String saksbehandlersValgteEnhet, String beskrivelse);

    boolean oppgaveErFerdigstilt(String oppgaveid);

    Oppgave finnOgTilordneSTOOppgave(String fnr, String henvendelseId, Temagruppe temagruppe, String enhet, boolean tvungenTilordning);

    class FikkIkkeTilordnet extends Exception {
        public FikkIkkeTilordnet(Throwable cause) {
            super(cause);
        }
    }
    class AlleredeTildeltAnnenSaksbehandler extends Exception {
        public AlleredeTildeltAnnenSaksbehandler(String message) {
            super(message);
        }
    }
}
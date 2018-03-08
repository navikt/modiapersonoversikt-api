package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.sbl.dialogarena.sporsmalogsvar.domain.NyOppgave;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSOppgave;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOptimistiskLasing;

public interface GsakService {
    boolean oppgaveKanManuelltAvsluttes(String oppgaveId);

    WSOppgave hentOppgave(String oppgaveId);

    void ferdigstillGsakOppgave(WSOppgave oppgave, String beskrivelse) throws LagreOppgaveOptimistiskLasing, OppgaveErFerdigstilt;

    void opprettGsakOppgave(NyOppgave nyOppgave);

    class OppgaveErFerdigstilt extends Exception {
        public OppgaveErFerdigstilt(Throwable cause) {
            super("Oppgaven er allerede ferdigstilt", cause);
        }
    }
}

package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GsakKodeTema;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.NyOppgave;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSOppgave;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOptimistiskLasing;

import java.util.List;

public interface GsakService {
    List<AnsattEnhet> hentForeslatteEnheter(String fnr, String tema, String type, Optional<GsakKodeTema.Underkategori> underkategori);

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

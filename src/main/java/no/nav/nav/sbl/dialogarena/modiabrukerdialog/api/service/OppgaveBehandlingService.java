package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service;

import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;

public interface OppgaveBehandlingService {

    void tilordneOppgaveIGsak(String oppgaveId, Temagruppe temagruppe) throws FikkIkkeTilordnet;

    Optional<Oppgave> plukkOppgaveFraGsak(Temagruppe temagruppe);

    void ferdigstillOppgaveIGsak(String oppgaveId, Temagruppe temagruppe);

    void leggTilbakeOppgaveIGsak(String oppgaveId, String beskrivelse, Temagruppe temagruppe);

    void systemLeggTilbakeOppgaveIGsak(String oppgaveId, Temagruppe temagruppe);

    boolean oppgaveErFerdigstilt(String oppgaveid);

    class FikkIkkeTilordnet extends Exception {
        public FikkIkkeTilordnet(Throwable cause) {
            super(cause);
        }
    }
}

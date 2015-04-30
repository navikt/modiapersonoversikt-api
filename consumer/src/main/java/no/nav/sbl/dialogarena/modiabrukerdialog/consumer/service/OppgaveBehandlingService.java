package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Oppgave;

public interface OppgaveBehandlingService {
    void tilordneOppgaveIGsak(String oppgaveId, Temagruppe temagruppe) throws FikkIkkeTilordnet;

    Optional<Oppgave> plukkOppgaveFraGsak(Temagruppe temagruppe);

    void ferdigstillOppgaveIGsak(String oppgaveId, Temagruppe temagruppe);

    void leggTilbakeOppgaveIGsak(Optional<String> oppgaveId, String beskrivelse, Optional<Temagruppe> temagruppe);

    void systemLeggTilbakeOppgaveIGsak(String oppgaveId, Temagruppe temagruppe);

    boolean oppgaveErFerdigstilt(String oppgaveid);

    public static class FikkIkkeTilordnet extends Exception {
        public FikkIkkeTilordnet(Throwable cause) {
            super(cause);
        }
    }
}

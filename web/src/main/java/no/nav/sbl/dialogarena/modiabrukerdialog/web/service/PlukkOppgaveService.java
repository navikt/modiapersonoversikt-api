package no.nav.sbl.dialogarena.modiabrukerdialog.web.service;

import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Oppgave;

public interface PlukkOppgaveService {
    Optional<Oppgave> plukkOppgave(Temagruppe temagruppe);

    boolean oppgaveErFerdigstilt(String oppgaveid);
}

package no.nav.sbl.dialogarena.modiabrukerdialog.web.service.plukkoppgave;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;

import java.util.List;


public interface PlukkOppgaveService {
    List<Oppgave> plukkOppgaver(Temagruppe temagruppe, String saksbehandlersValgteEnhet);
}

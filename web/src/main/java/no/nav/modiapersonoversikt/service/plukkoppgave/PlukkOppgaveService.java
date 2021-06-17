package no.nav.modiapersonoversikt.service.plukkoppgave;

import no.nav.modiapersonoversikt.legacy.api.domain.Oppgave;
import no.nav.modiapersonoversikt.legacy.api.domain.Temagruppe;

import java.util.List;


public interface PlukkOppgaveService {
    List<Oppgave> plukkOppgaver(Temagruppe temagruppe, String saksbehandlersValgteEnhet);
}

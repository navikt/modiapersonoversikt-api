package no.nav.modiapersonoversikt.service.plukkoppgave;

import no.nav.modiapersonoversikt.api.domain.Oppgave;
import no.nav.modiapersonoversikt.api.domain.Temagruppe;
import no.nav.modiapersonoversikt.api.service.OppgaveBehandlingService;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll;

import java.util.List;


public class PlukkOppgaveServiceImpl implements PlukkOppgaveService {

    private final OppgaveBehandlingService oppgaveBehandlingService;
    private final Tilgangskontroll tilgangskontroll;

    public PlukkOppgaveServiceImpl(OppgaveBehandlingService oppgaveBehandlingService, Tilgangskontroll tilgangskontroll) {
        this.oppgaveBehandlingService = oppgaveBehandlingService;
        this.tilgangskontroll = tilgangskontroll;
    }

    @Override
    public List<Oppgave> plukkOppgaver(Temagruppe temagruppe, String saksbehandlersValgteEnhet) {
        List<Oppgave> oppgaver = oppgaveBehandlingService.plukkOppgaverFraGsak(temagruppe, saksbehandlersValgteEnhet);
        if (!oppgaver.isEmpty() && !saksbehandlerHarTilgangTilBruker(oppgaver.get(0))) {
            return leggTilbakeOgPlukkNyeOppgaver(oppgaver, temagruppe, saksbehandlersValgteEnhet);
        }
        return oppgaver;
    }

    private List<Oppgave> leggTilbakeOgPlukkNyeOppgaver(List<Oppgave> oppgaver, Temagruppe temagruppe, String saksbehandlersValgteEnhet) {
        oppgaver.forEach(oppgave -> oppgaveBehandlingService.systemLeggTilbakeOppgaveIGsak(oppgave.oppgaveId, temagruppe, saksbehandlersValgteEnhet));
        return plukkOppgaver(temagruppe, saksbehandlersValgteEnhet);
    }

    private boolean saksbehandlerHarTilgangTilBruker(Oppgave oppgave) {
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(oppgave.fnr))
                .getDecision()
                .isPermit();
    }
}

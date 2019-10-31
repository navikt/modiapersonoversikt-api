package no.nav.sbl.dialogarena.modiabrukerdialog.web.service.plukkoppgave;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll;

import javax.inject.Inject;
import java.util.List;


public class PlukkOppgaveServiceImpl implements PlukkOppgaveService {

    private final OppgaveBehandlingService oppgaveBehandlingService;
    private final PersonKjerneinfoServiceBi personKjerneinfoServiceBi;
    private final Tilgangskontroll tilgangskontroll;

    public PlukkOppgaveServiceImpl(OppgaveBehandlingService oppgaveBehandlingService, PersonKjerneinfoServiceBi personKjerneinfoServiceBi, Tilgangskontroll tilgangskontroll) {
        this.oppgaveBehandlingService = oppgaveBehandlingService;
        this.personKjerneinfoServiceBi = personKjerneinfoServiceBi;
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

    @Override
    public boolean oppgaveErFerdigstilt(String oppgaveid) {
        return oppgaveBehandlingService.oppgaveErFerdigstilt(oppgaveid);
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

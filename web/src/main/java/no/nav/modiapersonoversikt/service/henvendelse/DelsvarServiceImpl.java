package no.nav.modiapersonoversikt.service.henvendelse;

import no.nav.modiapersonoversikt.legacy.api.domain.Kanal;
import no.nav.modiapersonoversikt.legacy.api.domain.Temagruppe;
import no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Fritekst;
import no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Melding;
import no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Meldingstype;
import no.nav.modiapersonoversikt.legacy.api.service.HenvendelseUtsendingService;
import no.nav.modiapersonoversikt.legacy.api.service.LeggTilbakeOppgaveIGsakRequest;
import no.nav.modiapersonoversikt.legacy.api.service.OppgaveBehandlingService;

import java.util.NoSuchElementException;
import java.util.Optional;

public class DelsvarServiceImpl implements DelsvarService {

    private final HenvendelseUtsendingService henvendelseUtsendingService;
    private final OppgaveBehandlingService oppgaveBehandlingService;

    public DelsvarServiceImpl(HenvendelseUtsendingService henvendelseUtsendingService, OppgaveBehandlingService oppgaveBehandlingService) {
        this.henvendelseUtsendingService = henvendelseUtsendingService;
        this.oppgaveBehandlingService = oppgaveBehandlingService;
    }

    public void svarDelvis(DelsvarRequest request) {
        Melding delsvar = lagDelsvar(hentBrukersSporsmal(request), request);
        try {
            leggTilbakePaaNyttTema(request);
            henvendelseUtsendingService
                    .ferdigstillHenvendelse(delsvar, Optional.empty(), Optional.empty(), request.behandlingsId, request.saksbehandlersValgteEnhet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Melding lagDelsvar(Melding brukersSporsmal, DelsvarRequest request) {
        return new Melding()
                .withKanal(Kanal.TEKST.name())
                .withFritekst(new Fritekst(request.svar))
                .withErTilknyttetAnsatt(true)
                .withTemagruppe(brukersSporsmal.temagruppe)
                .withTraadId(request.traadId)
                .withKontorsperretEnhet(brukersSporsmal.kontorsperretEnhet)
                .withType(Meldingstype.DELVIS_SVAR_SKRIFTLIG)
                .withFnr(brukersSporsmal.fnrBruker)
                .withNavIdent(request.navIdent)
                .withEksternAktor(request.navIdent)
                .withTilknyttetEnhet(request.saksbehandlersValgteEnhet)
                .withBrukersEnhet(brukersSporsmal.brukersEnhet);
    }

    private Melding hentBrukersSporsmal(DelsvarRequest request) {
        return henvendelseUtsendingService.hentTraad(request.fodselsnummer, request.traadId, request.saksbehandlersValgteEnhet).stream()
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    private void leggTilbakePaaNyttTema(DelsvarRequest request) {
        LeggTilbakeOppgaveIGsakRequest leggTilbakeOppgaveIGsakRequest = new LeggTilbakeOppgaveIGsakRequest()
                .withOppgaveId(request.oppgaveId)
                .withBeskrivelse("Henvendelsen er besvart delvis og lagt tilbake med ny temagruppe " + request.temagruppe)
                .withTemagruppe(getTemagruppefraRequest(request.temagruppe))
                .withSaksbehandlersValgteEnhet(request.saksbehandlersValgteEnhet);
        oppgaveBehandlingService.leggTilbakeOppgaveIGsak(leggTilbakeOppgaveIGsakRequest);
    }

    private Temagruppe getTemagruppefraRequest(String temagruppe) {
        try {
            return Temagruppe.valueOf(temagruppe);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Ugyldig temagruppe " + temagruppe);
        }
    }
}

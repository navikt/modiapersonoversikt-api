package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling;

import no.nav.common.auth.SubjectHandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.LeggTilbakeOppgaveIGsakRequest;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.ArbeidsfordelingV1Service;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSOppgave;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSUnderkategori;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOptimistiskLasing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ForbiddenException;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

class LeggTilbakeOppgaveIGsakDelegate {
    private final Logger log = LoggerFactory.getLogger(LeggTilbakeOppgaveIGsakDelegate.class);
    private final OppgaveBehandlingServiceImpl oppgaveBehandlingService;
    private final ArbeidsfordelingV1Service arbeidsfordelingService;

    LeggTilbakeOppgaveIGsakDelegate(OppgaveBehandlingServiceImpl oppgaveBehandlingService, ArbeidsfordelingV1Service arbeidsfordelingService) {
        this.oppgaveBehandlingService = oppgaveBehandlingService;
        this.arbeidsfordelingService = arbeidsfordelingService;
    }

    void leggTilbake(WSOppgave oppgaveFraGsak, LeggTilbakeOppgaveIGsakRequest request) {
        validerTilgang(oppgaveFraGsak);

        markerOppgaveSomLagtTilbake(oppgaveFraGsak, request);

        if (temagrupeErSatt(request.getNyTemagruppe())) {
            oppdaterForNyTemagruppe(oppgaveFraGsak, request.getNyTemagruppe());
        }

        lagreOppgaveIGsak(oppgaveFraGsak, request);
    }

    private void validerTilgang(WSOppgave oppgaveFraGsak) {
        String innloggetSaksbehandler = SubjectHandler.getIdent().orElseThrow(() -> new RuntimeException("Fant ikke ident"));
        if (!innloggetSaksbehandler.equals(oppgaveFraGsak.getAnsvarligId())) {
            String feilmelding = "Innlogget saksbehandler " + innloggetSaksbehandler
                    + " har ikke tilgang til oppgave " + oppgaveFraGsak.getOppgaveId()
                    + ". Oppgavens ansvarlige id er satt til : " + oppgaveFraGsak.getAnsvarligId() + ".";
            throw new ForbiddenException(feilmelding);
        }
    }

    private void markerOppgaveSomLagtTilbake(WSOppgave oppgaveFraGsak, LeggTilbakeOppgaveIGsakRequest request) {
        oppgaveFraGsak.withAnsvarligId("");
        oppgaveFraGsak.withBeskrivelse(lagNyBeskrivelse(oppgaveFraGsak, request));
    }

    private String lagNyBeskrivelse(WSOppgave oppgaveFraGsak, LeggTilbakeOppgaveIGsakRequest request) {
        return oppgaveBehandlingService.leggTilBeskrivelse(oppgaveFraGsak.getBeskrivelse(), request.getBeskrivelse(),
                request.getSaksbehandlersValgteEnhet());
    }

    private boolean temagrupeErSatt(Temagruppe temagruppe) {
        return temagruppe != null;
    }

    private void oppdaterForNyTemagruppe(WSOppgave oppgaveFraGsak, Temagruppe temagruppe) {
        oppgaveFraGsak.withAnsvarligEnhetId(getAnsvarligEnhet(oppgaveFraGsak, temagruppe));
        oppgaveFraGsak.withUnderkategori(getNyUnderkategori(temagruppe));
    }

    private WSUnderkategori getNyUnderkategori(Temagruppe temagruppe) {
        return new WSUnderkategori().withKode(underkategoriKode(temagruppe));
    }

    private String getAnsvarligEnhet(WSOppgave oppgaveFraGsak, Temagruppe temagruppe) {
        List<String> enheter = finnBehandlendeEnhetListe(oppgaveFraGsak, temagruppe).stream()
                .map(enhet -> enhet.enhetId)
                .collect(toList());
        return enheter.isEmpty() ? oppgaveFraGsak.getAnsvarligEnhetId() : enheter.get(0);
    }

    private List<AnsattEnhet> finnBehandlendeEnhetListe(WSOppgave oppgaveFraGsak, Temagruppe temagruppe) {
        try {
            return arbeidsfordelingService.finnBehandlendeEnhetListe(oppgaveFraGsak.getGjelder().getBrukerId(),
                    oppgaveFraGsak.getFagomrade().getKode(),
                    oppgaveFraGsak.getOppgavetype().getKode(),
                    underkategoriKode(temagruppe));
        } catch (Exception e) {
            log.error("Henting av behandlende enhet feilet", e);
            return Collections.emptyList();
        }
    }

    private void lagreOppgaveIGsak(WSOppgave oppgaveFraGsak, LeggTilbakeOppgaveIGsakRequest request) {
        try {
            oppgaveBehandlingService.lagreOppgaveIGsak(oppgaveFraGsak, request.getNyTemagruppe(), request.getSaksbehandlersValgteEnhet());
        } catch (LagreOppgaveOptimistiskLasing lagreOppgaveOptimistiskLasing) {
            throw new RuntimeException("Oppgaven kunne ikke lagres, den er for øyeblikket låst av en annen bruker.", lagreOppgaveOptimistiskLasing);
        }
    }

    private static String underkategoriKode(Temagruppe temagruppe) {
        return temagruppe + "_KNA";
    }
}

package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.oppgavebehandling;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.arbeidsfordeling.ArbeidsfordelingV1Service;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSOppgave;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSUnderkategori;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOptimistiskLasing;
import no.nav.virksomhet.tjenester.ruting.v1.Ruting;

import javax.ws.rs.NotAuthorizedException;
import java.util.List;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;

class LeggTilbakeOppgaveIGsakDelegate {

    private final OppgaveBehandlingServiceImpl oppgaveBehandlingService;
    private final Ruting ruting;
    private final ArbeidsfordelingV1Service arbeidsfordelingService;

    LeggTilbakeOppgaveIGsakDelegate(OppgaveBehandlingServiceImpl oppgaveBehandlingService, Ruting ruting, ArbeidsfordelingV1Service arbeidsfordelingService) {
        this.oppgaveBehandlingService = oppgaveBehandlingService;
        this.ruting = ruting;
        this.arbeidsfordelingService = arbeidsfordelingService;
    }

    void leggTilbake(WSOppgave oppgaveFraGsak, String beskrivelse, Temagruppe temagruppe) {
        validerTilgang(oppgaveFraGsak);

        markerOppgaveSomLagtTilbake(oppgaveFraGsak, beskrivelse);

        if (temagrupeErSatt(temagruppe)) {
            oppdaterForNyTemagruppe(oppgaveFraGsak, temagruppe);
        }

        lagreOppgaveIGsak(oppgaveFraGsak, temagruppe);
    }

    private void validerTilgang(WSOppgave oppgaveFraGsak) {
        String innloggetSaksbehandler = getSubjectHandler().getUid();
        if (!innloggetSaksbehandler.equals(oppgaveFraGsak.getAnsvarligId())) {
            throw new NotAuthorizedException("Innlogget saksbehandler " + innloggetSaksbehandler
                    + " har ikke tilgang til oppgave " + oppgaveFraGsak.getOppgaveId()
                    + ". Oppgavens ansvarlige id er satt til : " + oppgaveFraGsak.getAnsvarligId() + ".");
        }
    }

    private void markerOppgaveSomLagtTilbake(WSOppgave oppgaveFraGsak, String beskrivelse) {
        oppgaveFraGsak.withAnsvarligId("");
        oppgaveFraGsak.withBeskrivelse(lagNyBeskrivelse(oppgaveFraGsak, beskrivelse));
    }

    private String lagNyBeskrivelse(WSOppgave oppgaveFraGsak, String beskrivelse) {
        return oppgaveBehandlingService.leggTilBeskrivelse(oppgaveFraGsak.getBeskrivelse(), beskrivelse);
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
        List<String> enheter = finnBehandlendeEnhetListe(oppgaveFraGsak, temagruppe);
        return enheter.isEmpty() ? oppgaveFraGsak.getAnsvarligEnhetId() : enheter.get(0);
    }

    private List<String> finnBehandlendeEnhetListe(WSOppgave oppgaveFraGsak, Temagruppe temagruppe) {
        return arbeidsfordelingService.finnBehandlendeEnhetListe(oppgaveFraGsak.getGjelder().getBrukerId(),
                oppgaveFraGsak.getFagomrade().getKode(),
                oppgaveFraGsak.getOppgavetype().getKode(),
                underkategoriKode(temagruppe));
    }

    private void lagreOppgaveIGsak(WSOppgave oppgaveFraGsak, Temagruppe temagruppe) {
        try {
            oppgaveBehandlingService.lagreOppgaveIGsak(oppgaveFraGsak, temagruppe);
        } catch (LagreOppgaveOptimistiskLasing lagreOppgaveOptimistiskLasing) {
            throw new RuntimeException("Oppgaven kunne ikke lagres, den er for øyeblikket låst av en annen bruker.", lagreOppgaveOptimistiskLasing);
        }
    }

    private static String underkategoriKode(Temagruppe temagruppe) {
        return temagruppe + "_KNA";
    }
}

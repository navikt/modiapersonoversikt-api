package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.oppgavebehandling;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSOppgave;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSUnderkategori;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOptimistiskLasing;
import no.nav.virksomhet.tjenester.ruting.meldinger.v1.WSEnhet;
import no.nav.virksomhet.tjenester.ruting.meldinger.v1.WSFinnAnsvarligEnhetForOppgavetypeRequest;
import no.nav.virksomhet.tjenester.ruting.v1.Ruting;

import javax.ws.rs.NotAuthorizedException;
import java.util.List;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;

class LeggTilbakeOppgaveIGsakDelegate {

    private final OppgaveBehandlingServiceImpl oppgaveBehandlingService;
    private final Ruting ruting;

    private WSOppgave wsOppgave;

    LeggTilbakeOppgaveIGsakDelegate(OppgaveBehandlingServiceImpl oppgaveBehandlingService, Ruting ruting) {
        this.oppgaveBehandlingService = oppgaveBehandlingService;
        this.ruting = ruting;
    }

     void leggTilbake(String oppgaveId, String beskrivelse, Temagruppe temagruppe) {
        if (oppgaveId == null) {
            return;
        }

        this.wsOppgave = hentOppgaveFraGsak(oppgaveId);
        validerTilgang();

        markerOppgaveSomLagtTilbake(beskrivelse);

        if (temagrupeErSatt(temagruppe)) {
            oppdaterForNyTemagruppe(temagruppe);
        }

         lagreOppgaveIGsak(temagruppe);
     }

    private WSOppgave hentOppgaveFraGsak(String oppgaveId) {
        return oppgaveBehandlingService.hentOppgaveFraGsak(oppgaveId);
    }

    private void validerTilgang() {
        String innloggetSaksbehandler = getSubjectHandler().getUid();
        if (!innloggetSaksbehandler.equals(wsOppgave.getAnsvarligId())) {
            throw new NotAuthorizedException("Innlogget saksbehandler " + innloggetSaksbehandler
                    + " har ikke tilgang til oppgave " + wsOppgave.getOppgaveId()
                    + ". Oppgavens ansvarlige id er satt til : " + wsOppgave.getAnsvarligId() + ".");
        }
    }

    private void markerOppgaveSomLagtTilbake(String beskrivelse) {
        wsOppgave.withAnsvarligId("");
        wsOppgave.withBeskrivelse(lagNyBeskrivelse(beskrivelse));
    }

    private String lagNyBeskrivelse(String beskrivelse) {
        return oppgaveBehandlingService.leggTilBeskrivelse(wsOppgave.getBeskrivelse(), beskrivelse);
    }

    private boolean temagrupeErSatt(Temagruppe temagruppe) {
        return temagruppe != null;
    }

    private void oppdaterForNyTemagruppe(Temagruppe temagruppe) {
        wsOppgave.withAnsvarligEnhetId(getAnsvarligEnhet(temagruppe));
        wsOppgave.withUnderkategori(getNyUnderkategori(temagruppe));
    }

    private WSUnderkategori getNyUnderkategori(Temagruppe temagruppe) {
        return new WSUnderkategori().withKode(underkategoriKode(temagruppe));
    }

    private String getAnsvarligEnhet(Temagruppe temagruppe) {
        List<WSEnhet> enhetListe = finnAnsvarligEnhetForOppgavetype(temagruppe);
        return enhetListe.isEmpty() ? wsOppgave.getAnsvarligEnhetId() : enhetListe.get(0).getEnhetId();
    }

    private List<WSEnhet> finnAnsvarligEnhetForOppgavetype(Temagruppe temagruppe) {
        return ruting.finnAnsvarligEnhetForOppgavetype(
                new WSFinnAnsvarligEnhetForOppgavetypeRequest()
                        .withBrukerId(wsOppgave.getGjelder().getBrukerId())
                        .withOppgaveKode(wsOppgave.getOppgavetype().getKode())
                        .withFagomradeKode(wsOppgave.getFagomrade().getKode())
                        .withGjelderKode(underkategoriKode(temagruppe)))
                .getEnhetListe();
    }

    private void lagreOppgaveIGsak(Temagruppe temagruppe) {
        try {
            oppgaveBehandlingService.lagreOppgaveIGsak(wsOppgave, temagruppe);
        } catch (LagreOppgaveOptimistiskLasing lagreOppgaveOptimistiskLasing) {
            throw new RuntimeException("Oppgaven kunne ikke lagres, den er for øyeblikket låst av en annen bruker.", lagreOppgaveOptimistiskLasing);
        }
    }

    private static String underkategoriKode(Temagruppe temagruppe) {
        return temagruppe + "_KNA";
    }
}

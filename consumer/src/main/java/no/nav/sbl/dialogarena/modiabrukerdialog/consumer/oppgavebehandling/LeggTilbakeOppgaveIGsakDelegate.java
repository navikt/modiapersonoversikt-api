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

    private final WSOppgave oppgaveFraGsak;

    LeggTilbakeOppgaveIGsakDelegate(OppgaveBehandlingServiceImpl oppgaveBehandlingService, Ruting ruting, WSOppgave oppgaveFraGsak) {
        this.oppgaveBehandlingService = oppgaveBehandlingService;
        this.ruting = ruting;
        this.oppgaveFraGsak = oppgaveFraGsak;
    }

     void leggTilbake(String beskrivelse, Temagruppe temagruppe) {
        validerTilgang();

        markerOppgaveSomLagtTilbake(beskrivelse);

        if (temagrupeErSatt(temagruppe)) {
            oppdaterForNyTemagruppe(temagruppe);
        }

         lagreOppgaveIGsak(temagruppe);
     }

    private void validerTilgang() {
        String innloggetSaksbehandler = getSubjectHandler().getUid();
        if (!innloggetSaksbehandler.equals(oppgaveFraGsak.getAnsvarligId())) {
            throw new NotAuthorizedException("Innlogget saksbehandler " + innloggetSaksbehandler
                    + " har ikke tilgang til oppgave " + oppgaveFraGsak.getOppgaveId()
                    + ". Oppgavens ansvarlige id er satt til : " + oppgaveFraGsak.getAnsvarligId() + ".");
        }
    }

    private void markerOppgaveSomLagtTilbake(String beskrivelse) {
        oppgaveFraGsak.withAnsvarligId("");
        oppgaveFraGsak.withBeskrivelse(lagNyBeskrivelse(beskrivelse));
    }

    private String lagNyBeskrivelse(String beskrivelse) {
        return oppgaveBehandlingService.leggTilBeskrivelse(oppgaveFraGsak.getBeskrivelse(), beskrivelse);
    }

    private boolean temagrupeErSatt(Temagruppe temagruppe) {
        return temagruppe != null;
    }

    private void oppdaterForNyTemagruppe(Temagruppe temagruppe) {
        oppgaveFraGsak.withAnsvarligEnhetId(getAnsvarligEnhet(temagruppe));
        oppgaveFraGsak.withUnderkategori(getNyUnderkategori(temagruppe));
    }

    private WSUnderkategori getNyUnderkategori(Temagruppe temagruppe) {
        return new WSUnderkategori().withKode(underkategoriKode(temagruppe));
    }

    private String getAnsvarligEnhet(Temagruppe temagruppe) {
        List<WSEnhet> enhetListe = finnAnsvarligEnhetForOppgavetype(temagruppe);
        return enhetListe.isEmpty() ? oppgaveFraGsak.getAnsvarligEnhetId() : enhetListe.get(0).getEnhetId();
    }

    private List<WSEnhet> finnAnsvarligEnhetForOppgavetype(Temagruppe temagruppe) {
        return ruting.finnAnsvarligEnhetForOppgavetype(
                new WSFinnAnsvarligEnhetForOppgavetypeRequest()
                        .withBrukerId(oppgaveFraGsak.getGjelder().getBrukerId())
                        .withOppgaveKode(oppgaveFraGsak.getOppgavetype().getKode())
                        .withFagomradeKode(oppgaveFraGsak.getFagomrade().getKode())
                        .withGjelderKode(underkategoriKode(temagruppe)))
                .getEnhetListe();
    }

    private void lagreOppgaveIGsak(Temagruppe temagruppe) {
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

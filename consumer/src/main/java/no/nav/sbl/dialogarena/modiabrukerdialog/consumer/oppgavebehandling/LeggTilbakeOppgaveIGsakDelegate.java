package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.oppgavebehandling;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSOppgave;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSUnderkategori;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOptimistiskLasing;
import no.nav.virksomhet.tjenester.ruting.meldinger.v1.WSEnhet;
import no.nav.virksomhet.tjenester.ruting.meldinger.v1.WSFinnAnsvarligEnhetForOppgavetypeRequest;
import no.nav.virksomhet.tjenester.ruting.v1.Ruting;

import java.util.List;

class LeggTilbakeOppgaveIGsakDelegate {

    private final OppgaveBehandlingServiceImpl oppgaveBehandlingService;
    private final Ruting ruting;

     LeggTilbakeOppgaveIGsakDelegate(OppgaveBehandlingServiceImpl oppgaveBehandlingService, Ruting ruting) {
        this.oppgaveBehandlingService = oppgaveBehandlingService;
        this.ruting = ruting;
    }

     void leggTilbake(String oppgaveId, String beskrivelse, Temagruppe temagruppe) {
        if (oppgaveId == null) {
            return;
        }
        WSOppgave wsOppgave = oppgaveBehandlingService.hentOppgaveFraGsak(oppgaveId);
        wsOppgave.withAnsvarligId("");
        wsOppgave.withBeskrivelse(oppgaveBehandlingService.leggTilBeskrivelse(wsOppgave.getBeskrivelse(), beskrivelse));
        if (temagruppe != null) {
            List<WSEnhet> enhetListe = ruting.finnAnsvarligEnhetForOppgavetype(
                    new WSFinnAnsvarligEnhetForOppgavetypeRequest()
                            .withBrukerId(wsOppgave.getGjelder().getBrukerId())
                            .withOppgaveKode(wsOppgave.getOppgavetype().getKode())
                            .withFagomradeKode(wsOppgave.getFagomrade().getKode())
                            .withGjelderKode(underkategoriKode(temagruppe)))
                    .getEnhetListe();

            wsOppgave.withAnsvarligEnhetId(enhetListe.isEmpty() ? wsOppgave.getAnsvarligEnhetId() : enhetListe.get(0).getEnhetId());
            wsOppgave.withUnderkategori(new WSUnderkategori().withKode(underkategoriKode(temagruppe)));
        }

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

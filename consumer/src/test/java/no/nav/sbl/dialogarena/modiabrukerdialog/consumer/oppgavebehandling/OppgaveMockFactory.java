package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.oppgavebehandling;

import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.*;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSHentOppgaveResponse;

import static org.joda.time.DateTime.now;

class OppgaveMockFactory {

    public static final String ANSVARLIG_SAKSBEHANDLER = "z554455";
    public static final String OPPGAVE_ID = "oppgaveid";

    static WSHentOppgaveResponse mockHentOppgaveResponseMedTilordning() {
        return new WSHentOppgaveResponse().withOppgave(lagWSOppgave().withBeskrivelse("opprinnelig beskrivelse"));
    }

    static WSOppgave lagWSOppgave() {
        return new WSOppgave()
                .withOppgaveId(OPPGAVE_ID)
                .withAnsvarligId(ANSVARLIG_SAKSBEHANDLER)
                .withGjelder(new WSBruker().withBrukerId("***REMOVED***").withBrukertypeKode("brukertypekode"))
                .withDokumentId("dokumentid")
                .withKravId("kravid")
                .withAnsvarligEnhetId("ansvarligenhetid")

                .withFagomrade(new WSFagomrade().withKode("ARBD_KNA"))
                .withOppgavetype(new WSOppgavetype().withKode("wsOppgavetype"))
                .withPrioritet(new WSPrioritet().withKode("NORM_GEN"))
                .withUnderkategori(new WSUnderkategori().withKode("ARBEID_HJE"))

                .withAktivFra(now().toLocalDate())
                .withBeskrivelse("beskrivelse")
                .withVersjon(1)
                .withSaksnummer("saksnummer")
                .withStatus(new WSStatus().withKode("statuskode"))
                .withLest(false);
    }

}

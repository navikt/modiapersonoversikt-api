package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling;

import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.*;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSFinnOppgaveListeResponse;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSHentOppgaveResponse;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.joda.time.DateTime.now;

class OppgaveMockFactory {

    public static final String ANSVARLIG_SAKSBEHANDLER = "z554455";
    public static final String OPPGAVE_ID = "123123123";
    public static final String FNR_MOSS_TESTFAMILIEN = "07063000250";

    static WSHentOppgaveResponse mockHentOppgaveResponseMedTilordning() {
        return new WSHentOppgaveResponse().withOppgave(lagWSOppgave().withBeskrivelse("opprinnelig beskrivelse"));
    }

    static WSFinnOppgaveListeResponse mockFinnOppgaveListe() {
        return new WSFinnOppgaveListeResponse()
                .withOppgaveListe(
                        Stream
                                .generate(WSOppgave::new)
                                .limit(5)
                                .map(s -> lagWSOppgave())
                                .collect(Collectors.toList()));
    }

    static WSOppgave lagWSOppgave() {
        return new WSOppgave()
                .withOppgaveId(OPPGAVE_ID)
                .withAnsvarligId(ANSVARLIG_SAKSBEHANDLER)
                .withGjelder(new WSBruker().withBrukerId(FNR_MOSS_TESTFAMILIEN).withBrukertypeKode("brukertypekode"))
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

package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.virksomhet.gjennomforing.oppgave.v2.WSBruker;
import no.nav.virksomhet.gjennomforing.oppgave.v2.WSFagomrade;
import no.nav.virksomhet.gjennomforing.oppgave.v2.WSOppgave;
import no.nav.virksomhet.gjennomforing.oppgave.v2.WSOppgavetype;
import no.nav.virksomhet.gjennomforing.oppgave.v2.WSPrioritet;
import no.nav.virksomhet.gjennomforing.oppgave.v2.WSStatus;
import no.nav.virksomhet.gjennomforing.oppgave.v2.WSUnderkategori;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.WSFinnFeilregistrertOppgaveListeRequest;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.WSFinnFeilregistrertOppgaveListeResponse;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.WSFinnFerdigstiltOppgaveListeRequest;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.WSFinnFerdigstiltOppgaveListeResponse;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.WSFinnMappeListeRequest;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.WSFinnMappeListeResponse;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.WSFinnOppgaveListeRequest;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.WSFinnOppgaveListeResponse;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.WSHentOppgaveRequest;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.WSHentOppgaveResponse;
import no.nav.virksomhet.tjenester.oppgave.v2.HentOppgaveOppgaveIkkeFunnet;
import no.nav.virksomhet.tjenester.oppgave.v2.Oppgave;

import static org.joda.time.DateTime.now;

public class GsakOppgaveV2PortTypeMock {

    public static Oppgave createOppgavePortTypeMock() {
        return new Oppgave() {
            @Override
            public WSFinnFeilregistrertOppgaveListeResponse finnFeilregistrertOppgaveListe(WSFinnFeilregistrertOppgaveListeRequest request) {
                return new WSFinnFeilregistrertOppgaveListeResponse();
            }

            @Override
            public WSHentOppgaveResponse hentOppgave(WSHentOppgaveRequest request) throws HentOppgaveOppgaveIkkeFunnet {
                return new WSHentOppgaveResponse().withOppgave(lagWSOppgave(request.getOppgaveId()));
            }

            @Override
            public WSFinnMappeListeResponse finnMappeListe(WSFinnMappeListeRequest request) {
                return new WSFinnMappeListeResponse();
            }

            @Override
            public WSFinnOppgaveListeResponse finnOppgaveListe(WSFinnOppgaveListeRequest request) {
                return new WSFinnOppgaveListeResponse().withOppgaveListe(lagWSOppgave());
            }

            @Override
            public WSFinnFerdigstiltOppgaveListeResponse finnFerdigstiltOppgaveListe(WSFinnFerdigstiltOppgaveListeRequest request) {
                return new WSFinnFerdigstiltOppgaveListeResponse();
            }
        };
    }

    public static WSOppgave lagWSOppgave() {
        return lagWSOppgave("1");
    }

    public static WSOppgave lagWSOppgave(String oppgaveId) {
        return new WSOppgave()
                .withOppgaveId(oppgaveId)
                .withOppgavetype(new WSOppgavetype().withKode("wsOppgavetype"))
                .withGjelder(new WSBruker().withBrukerId("***REMOVED***"))
                .withStatus(new WSStatus().withKode("statuskode"))
                .withFagomrade(new WSFagomrade().withKode("HJE"))
                .withAktivFra(now().toLocalDate())
                .withPrioritet(new WSPrioritet().withKode("NORM_GEN"))
                .withUnderkategori(new WSUnderkategori().withKode("ARBEID_HJE"))
                .withLest(false)
                .withVersjon(1);
    }
}

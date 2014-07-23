package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.oppgave.v3.HentOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSBruker;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSFagomrade;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSOppgave;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSOppgavetype;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSPrioritet;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSStatus;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSUnderkategori;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSFinnFeilregistrertOppgaveListeRequest;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSFinnFeilregistrertOppgaveListeResponse;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSFinnFerdigstiltOppgaveListeRequest;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSFinnFerdigstiltOppgaveListeResponse;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSFinnMappeListeRequest;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSFinnMappeListeResponse;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSFinnOppgaveListeRequest;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSFinnOppgaveListeResponse;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSHentOppgaveRequest;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSHentOppgaveResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.joda.time.DateTime.now;

@Configuration
public class GsakOppgaveV3PortTypeMock {
    @Bean
    public static OppgaveV3 createOppgavePortTypeMock() {
        return new OppgaveV3() {
            @Override
            public void ping() {
            }

            @Override
            public WSFinnMappeListeResponse finnMappeListe(WSFinnMappeListeRequest request) {
                return new WSFinnMappeListeResponse();
            }

            @Override
            public WSFinnFeilregistrertOppgaveListeResponse finnFeilregistrertOppgaveListe(WSFinnFeilregistrertOppgaveListeRequest request) {
                return new WSFinnFeilregistrertOppgaveListeResponse();
            }

            @Override
            public WSFinnOppgaveListeResponse finnOppgaveListe(WSFinnOppgaveListeRequest request) {
                return new WSFinnOppgaveListeResponse().withOppgaveListe(lagWSOppgave());
            }

            @Override
            public WSHentOppgaveResponse hentOppgave(WSHentOppgaveRequest request) throws HentOppgaveOppgaveIkkeFunnet {
                return new WSHentOppgaveResponse().withOppgave(lagWSOppgave(request.getOppgaveId()));
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

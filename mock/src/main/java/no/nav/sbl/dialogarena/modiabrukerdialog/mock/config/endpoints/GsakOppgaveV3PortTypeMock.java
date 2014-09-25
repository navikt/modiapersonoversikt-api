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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class GsakOppgaveV3PortTypeMock {
    @Bean
    public static OppgaveV3 createOppgavePortTypeMock() {
        OppgaveV3 v3 = mock(OppgaveV3.class);
        try {
            when(v3.finnMappeListe(any(WSFinnMappeListeRequest.class)))
                    .thenReturn(new WSFinnMappeListeResponse());

            when(v3.finnFeilregistrertOppgaveListe(any(WSFinnFeilregistrertOppgaveListeRequest.class)))
                    .thenReturn(new WSFinnFeilregistrertOppgaveListeResponse());

            when(v3.finnOppgaveListe(any(WSFinnOppgaveListeRequest.class)))
                    .thenReturn(new WSFinnOppgaveListeResponse().withOppgaveListe(lagWSOppgave()));

            when(v3.hentOppgave(any(WSHentOppgaveRequest.class)))
                    .thenAnswer(new Answer<WSHentOppgaveResponse>() {
                        @Override
                        public WSHentOppgaveResponse answer(InvocationOnMock invocation) throws Throwable {
                            WSHentOppgaveRequest req = (WSHentOppgaveRequest) invocation.getArguments()[0];
                            return new WSHentOppgaveResponse().withOppgave(lagWSOppgave(req.getOppgaveId()));
                        }
                    });

            when(v3.finnFerdigstiltOppgaveListe(any(WSFinnFerdigstiltOppgaveListeRequest.class)))
                    .thenReturn(new WSFinnFerdigstiltOppgaveListeResponse());

        } catch (HentOppgaveOppgaveIkkeFunnet hentOppgaveOppgaveIkkeFunnet) {
            hentOppgaveOppgaveIkkeFunnet.printStackTrace();
        }

        return v3;
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

package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.oppgave.v3.HentOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.*;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.joda.time.DateTime.now;
import static org.mockito.ArgumentMatchers.any;
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
                        public WSHentOppgaveResponse answer(InvocationOnMock invocation) {
                            WSHentOppgaveRequest req = (WSHentOppgaveRequest) invocation.getArguments()[0];
                            return new WSHentOppgaveResponse().withOppgave(lagWSOppgave(req.getOppgaveId()));
                        }
                    });

            when(v3.finnFerdigstiltOppgaveListe(any(WSFinnFerdigstiltOppgaveListeRequest.class)))
                    .thenReturn(new WSFinnFerdigstiltOppgaveListeResponse());

        } catch (HentOppgaveOppgaveIkkeFunnet ignored) {
            throw new RuntimeException(ignored);
        }

        return v3;
    }

    public static WSOppgave lagWSOppgave() {
        return lagWSOppgave("1");
    }

    public static WSOppgave lagWSOppgave(String oppgaveId) {
        return new WSOppgave()
                .withOppgaveId(oppgaveId)
                .withHenvendelseId(HenvendelsePortTypeMock.BEHANDLINGS_ID1)
                .withOppgavetype(new WSOppgavetype().withKode("wsOppgavetype"))
                .withGjelder(new WSBruker().withBrukerId("10108000398"))
                .withStatus(new WSStatus().withKode("statuskode"))
                .withFagomrade(new WSFagomrade().withKode("HJE"))
                .withAktivFra(now().toLocalDate())
                .withPrioritet(new WSPrioritet().withKode("NORM_GEN"))
                .withUnderkategori(new WSUnderkategori().withKode("ARBEID_HJE"))
                .withLest(false)
                .withVersjon(1);
    }
}

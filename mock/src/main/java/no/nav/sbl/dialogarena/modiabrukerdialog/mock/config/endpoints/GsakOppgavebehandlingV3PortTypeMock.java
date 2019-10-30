package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.*;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GsakOppgavebehandlingV3PortTypeMock {

    @Bean
    public static OppgavebehandlingV3 createOppgavebehandlingPortTypeMock() {
        return new OppgavebehandlingV3() {
            @Override
            public WSFerdigstillOppgaveBolkResponse ferdigstillOppgaveBolk(WSFerdigstillOppgaveBolkRequest request) {
                return new WSFerdigstillOppgaveBolkResponse();
            }

            @Override
            public WSOpprettMappeResponse opprettMappe(WSOpprettMappeRequest request) {
                return new WSOpprettMappeResponse();
            }

            @Override
            public WSOpprettOppgaveBolkResponse opprettOppgaveBolk(WSOpprettOppgaveBolkRequest request) {
                return new WSOpprettOppgaveBolkResponse();
            }

            @Override
            public void ping() {

            }

            @Override
            public void slettMappe(WSSlettMappeRequest request) throws SlettMappeMappeIkkeTom, SlettMappeMappeIkkeFunnet {
            }

            @Override
            public void feilregistrerOppgave(WSFeilregistrerOppgaveRequest request) throws FeilregistrerOppgaveOppgaveIkkeFunnet, FeilregistrerOppgaveUlovligStatusOvergang {
            }

            @Override
            public WSLagreOppgaveBolkResponse lagreOppgaveBolk(WSLagreOppgaveBolkRequest request) {
                return new WSLagreOppgaveBolkResponse();
            }

            @Override
            public WSOpprettOppgaveResponse opprettOppgave(WSOpprettOppgaveRequest request) {
                return new WSOpprettOppgaveResponse();
            }

            @Override
            public WSTildelOppgaveResponse tildelOppgave(WSTildelOppgaveRequest request) {
                return new WSTildelOppgaveResponse();
            }

            @Override
            public void lagreOppgave(WSLagreOppgaveRequest request) throws LagreOppgaveOppgaveIkkeFunnet {
            }

            @Override
            public void lagreMappe(WSLagreMappeRequest request) throws LagreMappeMappeIkkeFunnet {
            }
        };
    }
}

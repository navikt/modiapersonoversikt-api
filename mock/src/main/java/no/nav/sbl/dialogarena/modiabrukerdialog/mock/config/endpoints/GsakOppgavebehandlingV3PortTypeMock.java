package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.FeilregistrerOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.FeilregistrerOppgaveUlovligStatusOvergang;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreMappeMappeIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.SlettMappeMappeIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.SlettMappeMappeIkkeTom;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSFeilregistrerOppgaveRequest;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSFerdigstillOppgaveBolkRequest;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSFerdigstillOppgaveBolkResponse;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSLagreMappeRequest;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSLagreOppgaveBolkRequest;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSLagreOppgaveBolkResponse;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSLagreOppgaveRequest;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSOpprettMappeRequest;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSOpprettMappeResponse;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSOpprettOppgaveBolkRequest;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSOpprettOppgaveBolkResponse;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSOpprettOppgaveRequest;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSOpprettOppgaveResponse;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSSlettMappeRequest;
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
            public void lagreOppgave(WSLagreOppgaveRequest request) throws LagreOppgaveOppgaveIkkeFunnet {
            }

            @Override
            public void lagreMappe(WSLagreMappeRequest request) throws LagreMappeMappeIkkeFunnet {
            }
        };
    }
}

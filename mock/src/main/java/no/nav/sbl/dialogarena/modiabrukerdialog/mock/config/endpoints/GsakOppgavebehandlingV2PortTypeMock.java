package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.WSBestillOppgaveRequest;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.WSBestillOppgaveResponse;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.WSFeilregistrerOppgaveRequest;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.WSFerdigstillOppgaveBolkRequest;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.WSFerdigstillOppgaveBolkResponse;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.WSLagreMappeRequest;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.WSLagreOppgaveBolkRequest;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.WSLagreOppgaveBolkResponse;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.WSLagreOppgaveRequest;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.WSOpprettMappeRequest;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.WSOpprettMappeResponse;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.WSOpprettOppgaveBolkRequest;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.WSOpprettOppgaveBolkResponse;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.WSOpprettOppgaveRequest;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.WSOpprettOppgaveResponse;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.WSSlettMappeRequest;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.BestillOppgaveIkkeEntydigSaksopprettelse;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.BestillOppgavePersonIkkeFunnet;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.BestillOppgavePersonInaktiv;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.BestillOppgaveUkjentArbeidsgiver;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.FeilregistrerOppgaveOppgaveIkkeFunnet;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.FeilregistrerOppgaveUlovligStatusOvergang;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.LagreMappeMappeIkkeFunnet;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.LagreOppgaveOppgaveIkkeFunnet;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.Oppgavebehandling;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.SlettMappeMappeIkkeFunnet;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.SlettMappeMappeIkkeTom;

public class GsakOppgavebehandlingV2PortTypeMock {
    public static Oppgavebehandling createOppgavebehandlingPortTypeMock() {
        return new Oppgavebehandling() {
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
            public WSBestillOppgaveResponse bestillOppgave(WSBestillOppgaveRequest request) throws BestillOppgavePersonIkkeFunnet, BestillOppgaveIkkeEntydigSaksopprettelse, BestillOppgavePersonInaktiv, BestillOppgaveUkjentArbeidsgiver {
                return new WSBestillOppgaveResponse();
            }

            @Override
            public void lagreMappe(WSLagreMappeRequest request) throws LagreMappeMappeIkkeFunnet {
            }
        };
    }
}

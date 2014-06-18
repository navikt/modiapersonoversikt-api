package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.BestillOppgaveRequest;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.BestillOppgaveResponse;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.FeilregistrerOppgaveRequest;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.FerdigstillOppgaveBolkRequest;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.FerdigstillOppgaveBolkResponse;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.LagreMappeRequest;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.LagreOppgaveBolkRequest;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.LagreOppgaveBolkResponse;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.LagreOppgaveRequest;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.OpprettMappeRequest;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.OpprettMappeResponse;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.OpprettOppgaveBolkRequest;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.OpprettOppgaveBolkResponse;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.OpprettOppgaveRequest;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.OpprettOppgaveResponse;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.SlettMappeRequest;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.binding.BestillOppgaveIkkeEntydigSaksopprettelse;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.binding.BestillOppgavePersonIkkeFunnet;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.binding.BestillOppgavePersonInaktiv;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.binding.BestillOppgaveUkjentArbeidsgiver;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.binding.FeilregistrerOppgaveOppgaveIkkeFunnet;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.binding.FeilregistrerOppgaveUlovligStatusOvergang;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.binding.LagreMappeMappeIkkeFunnet;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.binding.LagreOppgaveOppgaveIkkeFunnet;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.binding.Oppgavebehandling;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.binding.SlettMappeMappeIkkeFunnet;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.binding.SlettMappeMappeIkkeTom;

public class GsakOppgavebehandlingV2PortTypeMock {
    public static Oppgavebehandling createOppgavebehandlingPortTypeMock() {
        return new Oppgavebehandling() {
            @Override
            public void feilregistrerOppgave(FeilregistrerOppgaveRequest feilregistrerOppgaveRequest) throws FeilregistrerOppgaveOppgaveIkkeFunnet, FeilregistrerOppgaveUlovligStatusOvergang {
            }

            @Override
            public void lagreOppgave(LagreOppgaveRequest lagreOppgaveRequest) throws LagreOppgaveOppgaveIkkeFunnet {
            }

            @Override
            public OpprettOppgaveResponse opprettOppgave(OpprettOppgaveRequest opprettOppgaveRequest) {
                return new OpprettOppgaveResponse();
            }

            @Override
            public OpprettOppgaveBolkResponse opprettOppgaveBolk(OpprettOppgaveBolkRequest opprettOppgaveBolkRequest) {
                return new OpprettOppgaveBolkResponse();
            }

            @Override
            public FerdigstillOppgaveBolkResponse ferdigstillOppgaveBolk(FerdigstillOppgaveBolkRequest ferdigstillOppgaveBolkRequest) {
                return new FerdigstillOppgaveBolkResponse();
            }

            @Override
            public OpprettMappeResponse opprettMappe(OpprettMappeRequest opprettMappeRequest) {
                return new OpprettMappeResponse();
            }

            @Override
            public void lagreMappe(LagreMappeRequest lagreMappeRequest) throws LagreMappeMappeIkkeFunnet {
            }

            @Override
            public void slettMappe(SlettMappeRequest slettMappeRequest) throws SlettMappeMappeIkkeFunnet, SlettMappeMappeIkkeTom {
            }

            @Override
            public LagreOppgaveBolkResponse lagreOppgaveBolk(LagreOppgaveBolkRequest lagreOppgaveBolkRequest) {
                return new LagreOppgaveBolkResponse();
            }

            @Override
            public BestillOppgaveResponse bestillOppgave(BestillOppgaveRequest bestillOppgaveRequest)
                    throws BestillOppgaveIkkeEntydigSaksopprettelse, BestillOppgavePersonIkkeFunnet, BestillOppgavePersonInaktiv, BestillOppgaveUkjentArbeidsgiver {
                return new BestillOppgaveResponse();
            }
        };
    }
}

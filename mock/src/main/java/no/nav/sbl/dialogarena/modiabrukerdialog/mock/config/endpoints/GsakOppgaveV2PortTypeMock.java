package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.virksomhet.gjennomforing.oppgave.v2.Oppgavetype;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.FinnFeilregistrertOppgaveListeRequest;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.FinnFeilregistrertOppgaveListeResponse;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.FinnFerdigstiltOppgaveListeRequest;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.FinnFerdigstiltOppgaveListeResponse;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.FinnMappeListeRequest;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.FinnMappeListeResponse;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.FinnOppgaveListeRequest;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.FinnOppgaveListeResponse;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.HentOppgaveRequest;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.HentOppgaveResponse;
import no.nav.virksomhet.tjenester.oppgave.v2.binding.HentOppgaveOppgaveIkkeFunnet;
import no.nav.virksomhet.tjenester.oppgave.v2.binding.Oppgave;

public class GsakOppgaveV2PortTypeMock {

    public static Oppgave createOppgavePortTypeMock() {
        return new Oppgave() {
            @Override
            public HentOppgaveResponse hentOppgave(HentOppgaveRequest hentOppgaveRequest) throws HentOppgaveOppgaveIkkeFunnet {
                HentOppgaveResponse hentOppgaveResponse = new HentOppgaveResponse();
                no.nav.virksomhet.gjennomforing.oppgave.v2.Oppgave wsOppgave = new no.nav.virksomhet.gjennomforing.oppgave.v2.Oppgave();
                wsOppgave.setOppgaveId("1");
                Oppgavetype oppgavetype = new Oppgavetype();
                oppgavetype.setKode("kode");
                wsOppgave.setOppgavetype(oppgavetype);
                hentOppgaveResponse.setOppgave(wsOppgave);
                return hentOppgaveResponse;
            }

            @Override
            public FinnOppgaveListeResponse finnOppgaveListe(FinnOppgaveListeRequest finnOppgaveListeRequest) {
                return null;
            }

            @Override
            public FinnFerdigstiltOppgaveListeResponse finnFerdigstiltOppgaveListe(FinnFerdigstiltOppgaveListeRequest finnFerdigstiltOppgaveListeRequest) {
                return null;
            }

            @Override
            public FinnFeilregistrertOppgaveListeResponse finnFeilregistrertOppgaveListe(FinnFeilregistrertOppgaveListeRequest finnFeilregistrertOppgaveListeRequest) {
                return null;
            }

            @Override
            public FinnMappeListeResponse finnMappeListe(FinnMappeListeRequest finnMappeListeRequest) {
                return null;
            }
        };
    }
}

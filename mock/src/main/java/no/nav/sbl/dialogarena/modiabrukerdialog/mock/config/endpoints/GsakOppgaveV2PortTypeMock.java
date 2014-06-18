package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.virksomhet.gjennomforing.oppgave.v2.Bruker;
import no.nav.virksomhet.gjennomforing.oppgave.v2.Fagomrade;
import no.nav.virksomhet.gjennomforing.oppgave.v2.Oppgavetype;
import no.nav.virksomhet.gjennomforing.oppgave.v2.Status;
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

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.util.GregorianCalendar;

import static org.joda.time.DateTime.now;

public class GsakOppgaveV2PortTypeMock {

    public static Oppgave createOppgavePortTypeMock() {
        return new Oppgave() {
            @Override
            public HentOppgaveResponse hentOppgave(HentOppgaveRequest hentOppgaveRequest) throws HentOppgaveOppgaveIkkeFunnet {
                HentOppgaveResponse hentOppgaveResponse = new HentOppgaveResponse();
                no.nav.virksomhet.gjennomforing.oppgave.v2.Oppgave wsOppgave = new no.nav.virksomhet.gjennomforing.oppgave.v2.Oppgave();
                wsOppgave.setOppgaveId("1");
                Oppgavetype oppgavetype = new Oppgavetype();
                oppgavetype.setKode("oppgavetype");
                wsOppgave.setOppgavetype(oppgavetype);
                hentOppgaveResponse.setOppgave(wsOppgave);
                return hentOppgaveResponse;
            }

            @Override
            public FinnOppgaveListeResponse finnOppgaveListe(FinnOppgaveListeRequest finnOppgaveListeRequest) {
                FinnOppgaveListeResponse finnOppgaveListeResponse = new FinnOppgaveListeResponse();
                no.nav.virksomhet.gjennomforing.oppgave.v2.Oppgave wsOppgave = new no.nav.virksomhet.gjennomforing.oppgave.v2.Oppgave();
                wsOppgave.setOppgaveId("1");
                Oppgavetype wsOppgavetype = new Oppgavetype();
                wsOppgavetype.setKode("wsOppgavetype");
                wsOppgave.setOppgavetype(wsOppgavetype);
                Bruker wsBruker = new Bruker();
                wsBruker.setBrukerId("11111111111");
                wsOppgave.setGjelder(wsBruker);
                Status wsStatus = new Status();
                wsStatus.setKode("statuskode");
                wsOppgave.setStatus(wsStatus);
                Fagomrade wsFagomrade = new Fagomrade();
                wsFagomrade.setKode("fagomrade");
                wsOppgave.setFagomrade(wsFagomrade);
                GregorianCalendar gregorianCalendar = new GregorianCalendar();
                gregorianCalendar.setTime(now().toDate());
                try {
                    wsOppgave.setAktivFra(DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar));
                } catch (DatatypeConfigurationException e) {
                    e.printStackTrace();
                }
                wsOppgave.setVersjon(1);
                finnOppgaveListeResponse.getOppgaveListe().add(wsOppgave);
                return finnOppgaveListeResponse;
            }

            @Override
            public FinnFerdigstiltOppgaveListeResponse finnFerdigstiltOppgaveListe(FinnFerdigstiltOppgaveListeRequest finnFerdigstiltOppgaveListeRequest) {
                return new FinnFerdigstiltOppgaveListeResponse();
            }

            @Override
            public FinnFeilregistrertOppgaveListeResponse finnFeilregistrertOppgaveListe(FinnFeilregistrertOppgaveListeRequest finnFeilregistrertOppgaveListeRequest) {
                return new FinnFeilregistrertOppgaveListeResponse();
            }

            @Override
            public FinnMappeListeResponse finnMappeListe(FinnMappeListeRequest finnMappeListeRequest) {
                return new FinnMappeListeResponse();
            }
        };
    }
}

package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.virksomhet.gjennomforing.oppgave.v2.Bruker;
import no.nav.virksomhet.gjennomforing.oppgave.v2.Fagomrade;
import no.nav.virksomhet.gjennomforing.oppgave.v2.Mappe;
import no.nav.virksomhet.gjennomforing.oppgave.v2.Oppgavetype;
import no.nav.virksomhet.gjennomforing.oppgave.v2.Prioritet;
import no.nav.virksomhet.gjennomforing.oppgave.v2.Status;
import no.nav.virksomhet.gjennomforing.oppgave.v2.Underkategori;
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
                hentOppgaveResponse.setOppgave(lagWSOppgave(hentOppgaveRequest.getOppgaveId()));
                return hentOppgaveResponse;
            }

            @Override
            public FinnOppgaveListeResponse finnOppgaveListe(FinnOppgaveListeRequest finnOppgaveListeRequest) {
                FinnOppgaveListeResponse finnOppgaveListeResponse = new FinnOppgaveListeResponse();
                finnOppgaveListeResponse.getOppgaveListe().add(lagWSOppgave());
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

    public static no.nav.virksomhet.gjennomforing.oppgave.v2.Oppgave lagWSOppgave() {
        return lagWSOppgave("1");
    }

    public static no.nav.virksomhet.gjennomforing.oppgave.v2.Oppgave lagWSOppgave(String oppgaveId) {
        no.nav.virksomhet.gjennomforing.oppgave.v2.Oppgave wsOppgave = new no.nav.virksomhet.gjennomforing.oppgave.v2.Oppgave();
        wsOppgave.setOppgaveId(oppgaveId);
        Oppgavetype wsOppgavetype = new Oppgavetype();
        wsOppgavetype.setKode("wsOppgavetype");
        wsOppgave.setOppgavetype(wsOppgavetype);
        Bruker wsBruker = new Bruker();
        wsBruker.setBrukerId("***REMOVED***");
        wsOppgave.setGjelder(wsBruker);
        Status wsStatus = new Status();
        wsStatus.setKode("statuskode");
        wsOppgave.setStatus(wsStatus);
        Fagomrade wsFagomrade = new Fagomrade();
        wsFagomrade.setKode("HJE");
        wsOppgave.setFagomrade(wsFagomrade);
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(now().toDate());
        try {
            wsOppgave.setAktivFra(DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar));
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        Prioritet prioritet = new Prioritet();
        prioritet.setKode("NORM_GEN");
        Underkategori underkategori = new Underkategori();
        underkategori.setKode("ARBEID_HJE");
        wsOppgave.setUnderkategori(underkategori);
        wsOppgave.setPrioritet(prioritet);
        wsOppgave.setLest(false);
        wsOppgave.setVersjon(1);
        Mappe mappe = new Mappe();
        mappe.setMappeId("1");
        wsOppgave.setMappe(mappe);
        return wsOppgave;
    }
}

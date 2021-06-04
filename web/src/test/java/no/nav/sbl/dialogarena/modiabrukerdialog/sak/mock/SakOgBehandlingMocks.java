package no.nav.sbl.dialogarena.modiabrukerdialog.sak.mock;

import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.*;
import org.joda.time.DateTime;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import static org.joda.time.DateTime.now;

public class SakOgBehandlingMocks {

    public static final String TEMA = "DAG";

    public static Sak createWSSak() throws Exception {
        Sak sak = new Sak();
        sak.setSaksId("saksId-mock");
        Sakstemaer sakstemaer = new Sakstemaer();
        sakstemaer.setValue(TEMA);
        sakstemaer.setKodeverksRef("kodeverk-ref-mock");
        sak.getBehandlingskjede().add(createWSBehandlingskjede());

        sak.setOpprettet(toXMLCal(now()));

        return sak;
    }

    public static Behandlingskjede createWSBehandlingskjede() throws Exception {
        Behandlingskjede behandlingskjede = new Behandlingskjede();
        behandlingskjede.setBehandlingskjedeId("behandlingskjedeid-mock");
        behandlingskjede.setBehandlingstema(behandlingstemaer("kodeverk-ref-mock"));
        behandlingskjede.setBehandlingstema(behandlingstemaer("kodeverk-tema-mock"));
        behandlingskjede.setStart(toXMLCal(now()));
        behandlingskjede.setSisteBehandlingsstatus(behandlingsstatuser("avsluttet"));
        behandlingskjede.setSisteBehandlingREF("siste-behandling-ref-mock");
        behandlingskjede.setSisteBehandlingstype(behandlingstyper("behandlingstype-ref-mock"));
        behandlingskjede.setSisteBehandlingsstegREF("siste-behandling-steg-ref-mock");
        behandlingskjede.setSisteBehandlingsstegtype(behandlingsstegtyper("behandlingssteg-ref-mock"));

        return behandlingskjede;
    }

    private static Behandlingstemaer behandlingstemaer(String kodeverkref) {
        Behandlingstemaer behandlingstemaer = new Behandlingstemaer();
        behandlingstemaer.setKodeverksRef(kodeverkref);
        return behandlingstemaer;
    }

    private static Behandlingsstatuser behandlingsstatuser(String kodeverkref) {
        Behandlingsstatuser behandlingsstatuser = new Behandlingsstatuser();
        behandlingsstatuser.setKodeverksRef(kodeverkref);
        return behandlingsstatuser;
    }

    private static Behandlingstyper behandlingstyper(String kodeverkref) {
        Behandlingstyper behandlingstyper = new Behandlingstyper();
        behandlingstyper.setKodeverksRef(kodeverkref);
        return behandlingstyper;
    }

    private static Behandlingsstegtyper behandlingsstegtyper(String kodeverkref) {
        Behandlingsstegtyper behandlingsstegtyper = new Behandlingsstegtyper();
        behandlingsstegtyper.setKodeverksRef(kodeverkref);
        return behandlingsstegtyper;
    }

    public static XMLGregorianCalendar toXMLCal(DateTime date) throws DatatypeConfigurationException {
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(date.toString());
    }

}

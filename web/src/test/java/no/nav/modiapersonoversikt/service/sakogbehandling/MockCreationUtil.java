package no.nav.modiapersonoversikt.service.sakogbehandling;

import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.*;
import org.joda.time.DateTime;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import java.time.LocalDate;

import static org.joda.time.DateTime.now;

public class MockCreationUtil {

    public static Sak createWSSak() throws Exception {
        Sak sak = new Sak();
        sak.setSaksId("saksId-mock");
        Sakstemaer sakstemaer = new Sakstemaer();
        sakstemaer.setValue("DAG");
        sakstemaer.setKodeverksRef("kodeverk-ref-mock");
        sak.setSakstema(sakstemaer);
        sak.setOpprettet(toXMLCal(now()));

        Behandlingskjede behandling = createWSBehandlingskjede();
        Behandlingsstatuser status = new Behandlingsstatuser();
        status.setValue(FilterUtils.AVSLUTTET);
        behandling.setSisteBehandlingsstatus(status);

        Behandlingstyper type = new Behandlingstyper();
        type.setValue("ae0047");
        behandling.setSisteBehandlingstype(type);
        behandling.setSisteBehandlingsoppdatering(DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.now().toString()));

        sak.getBehandlingskjede().add(behandling);
        return sak;
    }

    public static Behandlingskjede createWSBehandlingskjede() throws Exception {
        Behandlingskjede behandlingskjede = new Behandlingskjede();
        behandlingskjede.setBehandlingskjedeId("behandlingskjedeid-mock");
        behandlingskjede.setBehandlingskjedetype(kodeverk(Behandlingskjedetyper.class, "kodeverk-ref-mock"));
        behandlingskjede.setBehandlingstema(kodeverk(Behandlingstemaer.class, "kodeverk-tema-mock"));
        behandlingskjede.setStart(toXMLCal(now()));
        behandlingskjede.setSisteBehandlingREF("siste-behandling-ref-mock");
        behandlingskjede.setSisteBehandlingstype(kodeverk(Behandlingstyper.class, "behandlingstype-ref-mock"));
        behandlingskjede.setSisteBehandlingsstegREF("siste-behandling-steg-ref-mock");
        behandlingskjede.setSisteBehandlingsstegtype(kodeverk(Behandlingsstegtyper.class, "behandlingssteg-ref-mock"));

        return behandlingskjede;
    }

    private static XMLGregorianCalendar toXMLCal(DateTime date) throws DatatypeConfigurationException {
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(date.toString());
    }

    private static <T extends Kodeverdi> T kodeverk(Class<T> type, String kodeverkref) {
        try {
            T t = type.getDeclaredConstructor().newInstance();
            t.setKodeRef(kodeverkref);
            return t;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

package no.nav.sbl.dialogarena.modiabrukerdialog.sak.mock;

import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.*;

import javax.xml.datatype.DatatypeConfigurationException;

import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.mock.SakOgBehandlingMocks.toXMLCal;
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

    private static <T extends Kodeverdi> T kodeverk(Class<T> type, String kodeverkref) {
        try {
            T t = type.newInstance();
            t.setKodeRef(kodeverkref);
            return t;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
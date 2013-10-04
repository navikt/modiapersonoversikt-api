package no.nav.sbl.dialogarena.soknader.service;

import no.nav.sbl.dialogarena.soknader.domain.Soknad;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingskjedetyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingstid;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Temaer;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class SoknaderServiceTest {


    @Mock
    private SakOgBehandlingPortType sakOgBehandlingPortType;

    @InjectMocks
    private SoknaderService soknaderService = new SoknaderService();

    @Test
    public void testGetSoknader() throws Exception {
        when(sakOgBehandlingPortType.finnSakOgBehandlingskjedeListe(any(FinnSakOgBehandlingskjedeListeRequest.class))).thenReturn(createResponse());
        List<Soknad> soknader = soknaderService.getSoknader("fnr");
        assertThat(soknader.size(), is(equalTo(1)));
    }


    private FinnSakOgBehandlingskjedeListeResponse createResponse(){
        FinnSakOgBehandlingskjedeListeResponse response = new FinnSakOgBehandlingskjedeListeResponse();
        Sak sak = new Sak();
        sak.setTema(new Temaer().withKodeRef("Dagpenger"));
        try {
            sak.setOpprettet(createXmlGregorianCalander());
            sak.setLukket(createXmlGregorianCalander());
            sak.setSaksId("id1");
            sak.withBehandlingskjede(createBehandlingKjede());
            response.withSak(sak);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    private XMLGregorianCalendar createXmlGregorianCalander() throws DatatypeConfigurationException {
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().toGregorianCalendar());
    }

    private Behandlingskjede createBehandlingKjede() throws DatatypeConfigurationException {
        Behandlingskjede behandlingskjede = new Behandlingskjede();
        behandlingskjede.withBehandlingskjedeId("behandling1");
        behandlingskjede.withNormertBehandlingstid(new Behandlingstid().withTid(BigInteger.valueOf(10)));
        behandlingskjede.withStart(createXmlGregorianCalander());
        behandlingskjede.withBehandlingskjedetype(new Behandlingskjedetyper().withKodeRef("tittel"));
        return behandlingskjede;
    }
}

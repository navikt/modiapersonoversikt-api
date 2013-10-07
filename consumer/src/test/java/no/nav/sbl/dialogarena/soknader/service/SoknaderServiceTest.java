package no.nav.sbl.dialogarena.soknader.service;

import no.nav.sbl.dialogarena.soknader.domain.Soknad;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingskjedetyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingstid;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingstidtyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Temaer;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

import static java.math.BigInteger.TEN;
import static javax.xml.datatype.DatatypeFactory.newInstance;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.joda.time.DateTime.now;
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
        assertThat(soknader.get(0).getTittel(), is(equalTo("tittel")));
    }

    private FinnSakOgBehandlingskjedeListeResponse createResponse() throws Exception{
        return new FinnSakOgBehandlingskjedeListeResponse()
                .withSak(createSak());
    }

    private Sak createSak()throws Exception {
        return new Sak()
                .withSaksId("id1")
                .withTema(new Temaer().withKodeRef("Dagpenger"))
                .withOpprettet(createXmlGregorianCalander())
                .withLukket(createXmlGregorianCalander())
                .withBehandlingskjede(createBehandlingKjede());
    }

    private Behandlingskjede createBehandlingKjede() throws Exception {
        return new Behandlingskjede()
                .withBehandlingskjedeId("behandling1")
                .withNormertBehandlingstid(createNormertBehandlingstid())
                .withStart(createXmlGregorianCalander())
                .withBehandlingskjedetype(new Behandlingskjedetyper().withKodeRef("tittel"));
    }

    private XMLGregorianCalendar createXmlGregorianCalander() throws Exception {
        return newInstance().newXMLGregorianCalendar(now().toGregorianCalendar());
    }

    private Behandlingstid createNormertBehandlingstid() {
        return new Behandlingstid().withTid(TEN).withType(new Behandlingstidtyper().withKodeRef("dager"));
    }
}

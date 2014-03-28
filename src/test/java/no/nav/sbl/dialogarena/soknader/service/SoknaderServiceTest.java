package no.nav.sbl.dialogarena.soknader.service;

import no.nav.sbl.dialogarena.soknader.domain.Soknad;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.WSBehandlingskjedetyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.WSBehandlingstemaer;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.WSBehandlingstid;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.WSBehandlingstidtyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.WSSakstemaer;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.math.BigInteger.TEN;
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
    public void testHentSoknader() throws Exception {
        when(sakOgBehandlingPortType.finnSakOgBehandlingskjedeListe(any(FinnSakOgBehandlingskjedeListeRequest.class))).thenReturn(createResponse());
        List<Soknad> soknader = soknaderService.hentSoknader("fnr");
        assertThat(soknader.size(), is(equalTo(1)));
        assertThat(soknader.get(0).getTittelKodeverk(), is(equalTo("a0001")));
    }

    @Test
    public void soknaderEldreEnn28DagerSkalIkkeVises() throws Exception {
        when(sakOgBehandlingPortType.finnSakOgBehandlingskjedeListe(any(FinnSakOgBehandlingskjedeListeRequest.class))).thenReturn(createResponse2());
        List<Soknad> soknader = soknaderService.hentSoknader("fnr");
        assertThat(soknader.size(), is(equalTo(1)));
        assertThat(soknader.get(0).getTittelKodeverk(), is(equalTo("a0001")));
    }

    private FinnSakOgBehandlingskjedeListeResponse createResponse2() throws Exception {
        return new FinnSakOgBehandlingskjedeListeResponse()
                .withSak(createSak(),createOldSak());
    }

    private FinnSakOgBehandlingskjedeListeResponse createResponse() throws Exception {
        return new FinnSakOgBehandlingskjedeListeResponse()
                .withSak(createSak());
    }

    private WSSak createOldSak() throws Exception {
        return new WSSak()
                .withSaksId("id1")
                .withSakstema(new WSSakstemaer().withValue("Dagpenger"))
                .withOpprettet(now().minusDays(40))
                .withLukket(now().minusDays(30))
                .withBehandlingskjede(createOldBehandlingKjede());
    }

    private WSBehandlingskjede createOldBehandlingKjede() {
        return new WSBehandlingskjede()
                .withBehandlingskjedeId("behandling1")
                .withNormertBehandlingstid(createNormertBehandlingstid())
                .withStart(now().minusDays(35))
                .withSluttNAVtid(now().minusDays(30))
                .withBehandlingstema(new WSBehandlingstemaer().withValue("a0002"))
                .withBehandlingskjedetype(new WSBehandlingskjedetyper().withValue("gammelTittel"));
    }

    private WSSak createSak() throws Exception {
        return new WSSak()
                .withSaksId("id1")
                .withSakstema(new WSSakstemaer().withValue("Dagpenger"))
                .withOpprettet(now())
                .withLukket(now())
                .withBehandlingskjede(createBehandlingKjede());
    }

    private WSBehandlingskjede createBehandlingKjede() throws Exception {
        return new WSBehandlingskjede()
                .withBehandlingskjedeId("behandling1")
                .withNormertBehandlingstid(createNormertBehandlingstid())
                .withStart(now())
                .withBehandlingstema(new WSBehandlingstemaer().withValue("a0001"))
                .withBehandlingskjedetype(new WSBehandlingskjedetyper().withValue("tittel"));
    }

    private WSBehandlingstid createNormertBehandlingstid() {
        return new WSBehandlingstid().withTid(TEN).withType(new WSBehandlingstidtyper().withKodeverksRef("dager"));
    }
}

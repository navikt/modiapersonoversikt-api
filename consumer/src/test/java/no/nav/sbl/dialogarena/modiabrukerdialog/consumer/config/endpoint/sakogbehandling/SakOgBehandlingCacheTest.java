package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.sakogbehandling;


import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandling_v1PortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse;
import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.sakogbehandling.SakOgBehandlingEndpointConfig.SAKOGBEHANDLING_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.TILLATMOCKSETUP_PROPERTY;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        SakOgBehandlingEndpointConfig.class
})
public class SakOgBehandlingCacheTest extends CacheTest {

    public static final String ENDPOINT_CACHE = "endpointCache";

    @Inject
    private SakOgBehandling_v1PortType sakOgBehandling;

    public SakOgBehandlingCacheTest() {
        super(ENDPOINT_CACHE);
    }

    @BeforeClass
    public static void setup() {
        //Problemfritt å kjøre med mock ettersom cacheannotasjon wrapper rundt switchingen
        System.setProperty(SAKOGBEHANDLING_KEY, "true");
        System.setProperty(TILLATMOCKSETUP_PROPERTY, "true");
        setupKeyAndTrustStore();
    }

    @Test
    public void cacheManager_harEntryForEndpointCache_etterKallTilHenvendelse() {
        FinnSakOgBehandlingskjedeListeRequest request1 = new FinnSakOgBehandlingskjedeListeRequest().withAktoerREF("aktoer");
        FinnSakOgBehandlingskjedeListeRequest request2 = new FinnSakOgBehandlingskjedeListeRequest().withAktoerREF("aktoer");
        WSSak sak1 = new WSSak().withOpprettet(DateTime.now());
        WSSak sak2 = new WSSak().withOpprettet(DateTime.now().plusDays(1));
        when(sakOgBehandling.finnSakOgBehandlingskjedeListe(any(FinnSakOgBehandlingskjedeListeRequest.class))).thenReturn(
                new FinnSakOgBehandlingskjedeListeResponse().withSak(sak1),
                new FinnSakOgBehandlingskjedeListeResponse().withSak(sak2)
        );

        FinnSakOgBehandlingskjedeListeResponse resp1 = sakOgBehandling.finnSakOgBehandlingskjedeListe(request1);
        FinnSakOgBehandlingskjedeListeResponse resp2 = sakOgBehandling.finnSakOgBehandlingskjedeListe(request2);

        assertThat(resp1.getSak().get(0).getOpprettet(), is(resp2.getSak().get(0).getOpprettet()));
    }
}

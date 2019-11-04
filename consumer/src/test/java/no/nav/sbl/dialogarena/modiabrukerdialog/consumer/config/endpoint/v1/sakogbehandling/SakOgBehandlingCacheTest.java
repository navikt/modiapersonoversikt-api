package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.sakogbehandling;


import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandling_v1PortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class SakOgBehandlingCacheTest extends CacheTest {

    private static final String ENDPOINT_CACHE = "endpointCache";

    @Inject
    private SakOgBehandling_v1PortType sakOgBehandling;

    SakOgBehandlingCacheTest() {
        super(ENDPOINT_CACHE);
    }

    @Test
    void cacheManager_harEntryForEndpointCache_etterKallTilHenvendelse() {
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

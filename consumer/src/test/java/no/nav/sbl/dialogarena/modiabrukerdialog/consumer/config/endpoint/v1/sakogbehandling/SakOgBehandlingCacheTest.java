package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.sakogbehandling;


import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util.CacheTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.SakBuilder;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.binding.SakOgBehandlingV1;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak;
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
    private SakOgBehandlingV1 sakOgBehandling;

    SakOgBehandlingCacheTest() {
        super(ENDPOINT_CACHE);
    }

    @Test
    void cacheManager_harEntryForEndpointCache_etterKallTilHenvendelse() {
        FinnSakOgBehandlingskjedeListeRequest request1 = new FinnSakOgBehandlingskjedeListeRequest();
        request1.setAktoerREF("aktoer");

        FinnSakOgBehandlingskjedeListeRequest request2 = new FinnSakOgBehandlingskjedeListeRequest();
        request2.setAktoerREF("aktoer");

        Sak sak1 = SakBuilder.create().withOpprettet(DateTime.now()).build();
        Sak sak2 = SakBuilder.create().withOpprettet(DateTime.now().plusDays(1)).build();

        FinnSakOgBehandlingskjedeListeResponse response1 = new FinnSakOgBehandlingskjedeListeResponse();
        response1.getSak().add(sak1);

        FinnSakOgBehandlingskjedeListeResponse response2 = new FinnSakOgBehandlingskjedeListeResponse();
        response2.getSak().add(sak2);


        when(sakOgBehandling.finnSakOgBehandlingskjedeListe(any(FinnSakOgBehandlingskjedeListeRequest.class))).thenReturn(
                response1,
                response2
        );

        FinnSakOgBehandlingskjedeListeResponse resp1 = sakOgBehandling.finnSakOgBehandlingskjedeListe(request1);
        FinnSakOgBehandlingskjedeListeResponse resp2 = sakOgBehandling.finnSakOgBehandlingskjedeListe(request2);

        assertThat(resp1.getSak().get(0).getOpprettet(), is(resp2.getSak().get(0).getOpprettet()));
    }
}

package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.sakogbehandling;


import no.nav.modig.cache.CacheConfig;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandling_v1PortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.sakogbehandling.SakOgBehandlingEndpointConfig.SAKOGBEHANDLING_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.MockUtil.TILLATMOCKSETUP_PROPERTY;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        CacheConfig.class,
        SakOgBehandlingEndpointConfig.class
})
public class SakOgBehandlingCacheTest {

    @Inject
    private EhCacheCacheManager cm;

    @Inject
    private SakOgBehandling_v1PortType sakOgBehandling;

    @BeforeClass
    public static void setup() {
        //Problemfritt å kjøre med mock ettersom cacheannotasjon wrapper rundt switchingen
        System.setProperty(SAKOGBEHANDLING_KEY, "true");
        System.setProperty(TILLATMOCKSETUP_PROPERTY, "true");
        setupKeyAndTrustStore();
    }

    @Test
    public void cacheManager_harEntryForEndpointCache_etterKallTilHenvendelse() {
        FinnSakOgBehandlingskjedeListeRequest cacheKey = new FinnSakOgBehandlingskjedeListeRequest().withAktoerREF("aktoer");
        sakOgBehandling.finnSakOgBehandlingskjedeListe(cacheKey);

        Object fromCache = cm.getCache("endpointCache").get(cacheKey).get();

        assertThat(fromCache, notNullValue());
    }

    @After
    public void shutdown() {
        cm.getCacheManager().shutdown();
    }

}

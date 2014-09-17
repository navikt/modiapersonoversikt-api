package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.kodeverk;


import no.nav.modig.cache.CacheConfig;
import no.nav.tjeneste.virksomhet.kodeverk.v2.HentKodeverkHentKodeverkKodeverkIkkeFunnet;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkRequest;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        CacheConfig.class,
        KodeverkV2EndpointConfig.class,
        KodeverkWrapperTestConfig.class
})
public class KodeverkCacheTest {

    @Inject
    private EhCacheCacheManager cm;

    @Inject
    private KodeverkPortType kodeverk;

    @Test
    public void cacheManager_harEntryForKodeverk_etterKallTilKodeverk() throws HentKodeverkHentKodeverkKodeverkIkkeFunnet {
        XMLHentKodeverkRequest cacheKey = new XMLHentKodeverkRequest()
                .withNavn("navn");
        kodeverk.hentKodeverk(cacheKey);

        Object fromCache = cm.getCache("kodeverkCache").get(cacheKey).get();

        assertThat(fromCache, notNullValue());
    }

    @After
    public void shutdown() {
        cm.getCacheManager().shutdown();
    }

}

package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.aktor;

import no.nav.modig.cache.CacheConfig;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.aktor.AktorEndpointConfig.AKTOER_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.TILLATMOCKSETUP_PROPERTY;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        CacheConfig.class,
        AktorEndpointConfig.class
})
public class AktorCacheTest {

    @Inject
    private EhCacheCacheManager cm;

    @Inject
    private AktoerPortType aktoer;

    @BeforeClass
    public static void setup() {
        //Problemfritt å kjøre med mock ettersom cacheannotasjon wrapper rundt switchingen
        System.setProperty(AKTOER_KEY, "true");
        System.setProperty(TILLATMOCKSETUP_PROPERTY, "true");
        setupKeyAndTrustStore();
    }

    @Test
    public void cacheManager_harEntryForAktorCache_etterKallTilAktor() throws HentAktoerIdForIdentPersonIkkeFunnet {
        HentAktoerIdForIdentRequest cacheKey = new HentAktoerIdForIdentRequest("242424 55555");
        aktoer.hentAktoerIdForIdent(cacheKey);

        Object fromCache = cm.getCache("aktorIdCache").get(cacheKey).get();

        assertThat(fromCache, notNullValue());
    }

    @After
    public void shutdown() {
        cm.getCacheManager().shutdown();
    }

}

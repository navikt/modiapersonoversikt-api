package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.henvendelsesoknader;

import no.nav.modig.cache.CacheConfig;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.henvendelsesoknader.HenvendelseSoknaderEndpointConfig.HENVENDELSESOKNADER_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.TILLATMOCKSETUP_PROPERTY;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        CacheConfig.class,
        HenvendelseSoknaderEndpointConfig.class
        })
public class HenvendelseSoknaderCacheTest {

    @Inject
    private EhCacheCacheManager cm;

    @Inject
    private HenvendelseSoknaderPortType henvendelse;

    @BeforeClass
    public static void setup() {
        //Problemfritt å kjøre med mock ettersom cacheannotasjon wrapper rundt switchingen
        System.setProperty(HENVENDELSESOKNADER_KEY, "true");
        System.setProperty(TILLATMOCKSETUP_PROPERTY, "true");
        setupKeyAndTrustStore();
    }

    @Test
    public void cacheManager_harEntryForEndpointCache_etterKallTilHenvendelse() {
        String cacheKey = "string";
        henvendelse.hentSoknadListe(cacheKey);

        Object fromCache = cm.getCache("endpointCache").get(cacheKey).get();

        assertThat(fromCache, notNullValue());
    }

    @After
    public void shutdown() {
        cm.getCacheManager().shutdown();
    }

}

package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.cms;

import no.nav.modig.cache.CacheConfig;
import no.nav.modig.content.ContentRetriever;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.testconfig.ProperySourcesPlaceholderConfigurer;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.net.URI;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CacheConfig.class, CmsEndpointConfig.class, ProperySourcesPlaceholderConfigurer.class})
public class CmsCacheTest {

    @Inject
    private ContentRetriever cms;

    @Inject
    private EhCacheCacheManager cm;

    @BeforeClass
    public static void setup() {
        System.setProperty("appres.cms.url", "http://www.nav.no/");
    }

    @Test
    public void cacheManager_harEntryForCms_etterKallTilCms() throws Exception {
        URI uri = new URI("http://www.nav.no/");
        cms.getContent(uri);

        Object fromCache = cm.getCache("cms.content").get(uri).get();

        assertThat(fromCache, notNullValue());
    }

    @After
    public void shutdown() {
        cm.getCacheManager().shutdown();
    }

}


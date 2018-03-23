package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.cms;

import no.nav.modig.cache.CacheConfig;
import no.nav.modig.content.ContentRetriever;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.net.URI;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        CacheConfig.class,
//        CmsEndpointConfig.class,
        ProperySourcesPlaceholderConfigurer.class})
public class CmsCacheTest {

//    @Inject
//    private ContentRetriever cms;
    @Inject
    private EhCacheCacheManager cm;

    @BeforeAll
    public static void setup() {
        System.setProperty("appres.cms.url", "http://www.nav.no/");
    }

    @Test
    @Disabled
    public void cacheManager_harEntryForCms_etterKallTilCms() throws Exception {
        URI uri = new URI("http://www.nav.no/");
//        cms.getContent(uri);

        Object fromCache = cm.getCache("cms.content").get(uri).get();

        assertThat(fromCache, notNullValue());
    }

    @AfterEach
    public void shutdown() {
        cm.getCacheManager().shutdown();
    }

}


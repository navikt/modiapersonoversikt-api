package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util;

import net.sf.ehcache.Ehcache;
import no.nav.brukerdialog.security.context.ThreadLocalSubjectHandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.cache.CacheConfiguration;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;

import static no.nav.brukerdialog.security.context.SubjectHandler.SUBJECTHANDLER_KEY;

@ContextConfiguration(classes = {CacheConfiguration.class})
public abstract class CacheTest {
    protected static EhCacheCacheManager cm;

    protected final String cachename;

    public CacheTest(String cachename) {
        this.cachename = cachename;
    }

    @Inject
    public void setEhCacheCacheManager(EhCacheCacheManager eccm) {
        cm = eccm;
    }

    @AfterClass
    public static void after() {
        cm.getCacheManager().shutdown();
    }

    @BeforeClass
    public static void before() {
        System.setProperty(SUBJECTHANDLER_KEY, ThreadLocalSubjectHandler.class.getName());
    }

    @Before
    public void teardown() {
        getCache().removeAll();
//        getCache().getCacheManager().getCache(cachename).setStatisticsEnabled(true);
//        getCache().getCacheManager().getCache(cachename).clearStatistics();
    }

    protected Ehcache getCache() {
        return ((EhCacheCache) cm.getCache(cachename)).getNativeCache();
    }
}

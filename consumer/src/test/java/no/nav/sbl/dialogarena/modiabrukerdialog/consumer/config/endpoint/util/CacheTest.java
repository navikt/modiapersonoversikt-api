package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.util;

import net.sf.ehcache.Ehcache;
import no.nav.metrics.proxy.MetricProxy;
import no.nav.metrics.proxy.TimerProxy;
import no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.cache.CacheConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.aop.framework.Advised;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;


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

    @AfterAll
    public static void after() {
        cm.getCacheManager().shutdown();
    }

    @BeforeAll
    public static void before() {
//        System.setProperty(SUBJECTHANDLER_KEY, ThreadLocalSubjectHandler.class.getName());
        System.setProperty("no.nav.modig.security.sts.url", "");
        System.setProperty("no.nav.modig.security.systemuser.username", "");
        System.setProperty("no.nav.modig.security.systemuser.password", "");
    }

    @BeforeEach
    public void teardown() {
        getCache().removeAll();
    }

    protected Ehcache getCache() {
        return ((EhCacheCache) cm.getCache(cachename)).getNativeCache();
    }

    protected static Object unwrapProxy(Object proxy) throws Exception {
        Advised advised = (Advised) proxy;

        Field invocationHandler = Proxy.class.getDeclaredField("h");
        Field object = MetricProxy.class.getDeclaredField("object");
        Field alternative = InstanceSwitcher.class.getDeclaredField("alternative");

        invocationHandler.setAccessible(true);
        object.setAccessible(true);
        alternative.setAccessible(true);

        TimerProxy timerProxy = (TimerProxy) invocationHandler.get(advised.getTargetSource().getTarget());
        InstanceSwitcher instanceSwitcher = (InstanceSwitcher) invocationHandler.get(object.get(timerProxy));

        return alternative.get(instanceSwitcher);
    }

}

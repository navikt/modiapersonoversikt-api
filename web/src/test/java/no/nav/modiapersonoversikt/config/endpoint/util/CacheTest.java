package no.nav.modiapersonoversikt.config.endpoint.util;

import no.nav.modiapersonoversikt.infrastructure.cache.CacheConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CacheConfiguration.class, CacheTestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class CacheTest {
    private static CacheManager cm;

    private final String cachename;

    public CacheTest(String cachename) {
        this.cachename = cachename;
    }

    @Autowired
    public void setCacheCacheManager(CacheManager cm) {
        CacheTest.cm = cm;
    }

    @BeforeEach
    public void teardown() {
        getCache().clear();
    }

    protected Cache getCache() {
        return cm.getCache(cachename);
    }

    protected com.github.benmanes.caffeine.cache.Cache<Object, Object> getNativeCache() {
        return (com.github.benmanes.caffeine.cache.Cache<Object, Object>) cm.getCache(cachename).getNativeCache();
    }

    protected static Object unwrapProxy(Object proxy) {
        return AopProxyUtils.getSingletonTarget(proxy);
    }
}

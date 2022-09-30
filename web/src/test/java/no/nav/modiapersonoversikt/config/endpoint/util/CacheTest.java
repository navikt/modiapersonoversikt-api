package no.nav.modiapersonoversikt.config.endpoint.util;

import no.nav.modiapersonoversikt.infrastructure.cache.CacheConfig;
import no.nav.modiapersonoversikt.utils.TestUtils;
import no.nav.modiapersonoversikt.utils.TestUtils.UnsafeRunneable;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CacheConfig.class, CacheTestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class CacheTest {
    static class ContainingMatcher extends BaseMatcher<Object> {
        private final String[] mustContain;

        ContainingMatcher(String ...mustContain) {
            this.mustContain = mustContain;
        }

        @Override
        public boolean matches(Object o) {
            if (o instanceof String key) {
                return Arrays.stream(mustContain).allMatch(key::contains);
            } else {
                return false;
            }
        }

        @Override
        public void describeTo(Description description) {
            description.appendValue("key did not include \"" + Arrays.stream(mustContain).collect(Collectors.joining(", ")) + "\"");
        }
    }

    public static Matcher<Object> generatedByUserKeyGenerator() {
        return new ContainingMatcher("user: ", " cachekey: ");
    }

    public static Matcher<Object> generatedByMethodAwareKeyGenerator() {
        return new ContainingMatcher("cachekey: ");
    }

    public static Matcher<Object> generatedByDefaultKeyGenerator() {
        return Matchers.allOf(
                Matchers.not(generatedByUserKeyGenerator()),
                Matchers.not(generatedByMethodAwareKeyGenerator())
        );
    }

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

    protected Object getKey() {
        return getNativeCache()
                .asMap().keySet()
                .stream().findFirst()
                .orElse(null);
    }

    protected void verifyUniqueAndStableCacheKeys(UnsafeRunneable... runnables) {
        long length = runnables.length;
        for (UnsafeRunneable runnable : runnables) {
            TestUtils.sneaky(runnable);
            // Running twice to verify stability
            TestUtils.sneaky(runnable);
        }
        assertThat("Cache-size should equal the number of operations in test", getNativeCache().estimatedSize(), Matchers.is(length));
    }

    protected static <T> T unwrapProxy(T proxy) {
        return (T) AopProxyUtils.getSingletonTarget(proxy);
    }
}

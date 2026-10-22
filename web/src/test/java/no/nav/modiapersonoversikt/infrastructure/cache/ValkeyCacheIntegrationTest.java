package no.nav.modiapersonoversikt.infrastructure.cache;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import no.nav.common.utils.fn.UnsafeRunnable;
import no.nav.common.utils.fn.UnsafeSupplier;
import no.nav.modiapersonoversikt.testutils.AuthContextTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@EnabledIfSystemProperty(named = "valkey.integration.enabled", matches = "true")
class ValkeyCacheIntegrationTest {
    private static final String CACHE_NAMESPACE = "test-" + UUID.randomUUID();

    @Test
    void sharesCachedValuesAcrossContextsButNotUsersOrMethods() {
        try (var forste = context("first"); var andre = context("second")) {
            var forsteTjeneste = forste.getBean(ProbeService.class);
            var andreTjeneste = andre.getBean(ProbeService.class);

            assertThat(AuthContextTestUtils.withIdent("Z123456", (UnsafeSupplier<String>) () -> forsteTjeneste.person("12345678901")))
                    .isEqualTo("first");
            try (var tilkobling = forste.getBean(RedisConnectionFactory.class).getConnection()) {
                var nokler = tilkobling.keyCommands().keys(("cache:v2:*:" + CACHE_NAMESPACE + ":*").getBytes(StandardCharsets.UTF_8));
                assertThat(nokler).isNotEmpty();
                for (var nokkel : nokler) {
                    var navn = new String(nokkel, StandardCharsets.UTF_8);
                    assertThat(navn).startsWith("cache:v2:");
                    assertThat(navn).contains("\"Z123456\"", "\"12345678901\"");
                }
            }
            assertThat(AuthContextTestUtils.withIdent("Z123456", (UnsafeSupplier<String>) () -> andreTjeneste.person("12345678901")))
                    .isEqualTo("first");
            assertThat(((ProbeService) AopProxyUtils.getSingletonTarget(andreTjeneste)).personKall.get()).isZero();

            assertThat(AuthContextTestUtils.withIdent("Z654321", (UnsafeSupplier<String>) () -> andreTjeneste.person("12345678901")))
                    .isEqualTo("second");
            assertThat(AuthContextTestUtils.withIdent("Z123456", (UnsafeSupplier<String>) () -> andreTjeneste.otherPerson("12345678901")))
                    .isEqualTo("other-second");
            assertThat(((ProbeService) AopProxyUtils.getSingletonTarget(andreTjeneste)).personKall.get()).isEqualTo(1);
        }
    }

    @Test
    void preservesCollectionsAndDoesNotCacheNulls() {
        try (var forste = context("first"); var andre = context("second")) {
            var forsteTjeneste = forste.getBean(ProbeService.class);
            var andreTjeneste = andre.getBean(ProbeService.class);

            assertThat(AuthContextTestUtils.withIdent("Z123456", (UnsafeSupplier<List<String>>) () -> forsteTjeneste.values("12345678901")))
                    .containsExactly("first", "12345678901");
            assertThat(AuthContextTestUtils.withIdent("Z123456", (UnsafeSupplier<List<String>>) () -> andreTjeneste.values("12345678901")))
                    .containsExactly("first", "12345678901");
            assertThat(((ProbeService) AopProxyUtils.getSingletonTarget(andreTjeneste)).verdiKall.get()).isZero();

            AuthContextTestUtils.withIdent("Z123456", (UnsafeRunnable) () -> forsteTjeneste.empty("12345678901"));
            AuthContextTestUtils.withIdent("Z123456", (UnsafeRunnable) () -> forsteTjeneste.empty("12345678901"));
            assertThat(((ProbeService) AopProxyUtils.getSingletonTarget(forsteTjeneste)).tomKall.get()).isEqualTo(2);
        }
    }

    private AnnotationConfigApplicationContext context(String etikett) {
        var kontekst = new AnnotationConfigApplicationContext();
        TestPropertyValues.of(
                "cache.backend=valkey",
                "APP_ENVIRONMENT_NAME=" + CACHE_NAMESPACE,
                "cache.valkey.host=" + System.getProperty("valkey.test.host", "127.0.0.1"),
                "cache.valkey.port=" + System.getProperty("valkey.test.port", "16379"),
                "cache.valkey.username=default",
                "cache.valkey.password=" + System.getProperty("valkey.test.password", "test-password"),
                "cache.valkey.ssl=false"
        ).applyTo(kontekst);
        kontekst.register(CacheConfig.class);
        kontekst.registerBean(MeterRegistry.class, SimpleMeterRegistry::new);
        kontekst.registerBean(ProbeService.class, () -> new ProbeService(etikett));
        kontekst.refresh();
        return kontekst;
    }

    static class ProbeService {
        final AtomicInteger personKall = new AtomicInteger();
        final AtomicInteger verdiKall = new AtomicInteger();
        final AtomicInteger tomKall = new AtomicInteger();
        final String etikett;

        ProbeService(String etikett) {
            this.etikett = etikett;
        }

        @Cacheable(cacheNames = "pdlCache", keyGenerator = "userkeygenerator")
        public String person(String ident) {
            personKall.incrementAndGet();
            return etikett;
        }

        @Cacheable(cacheNames = "pdlCache", keyGenerator = "userkeygenerator")
        public String otherPerson(String ident) {
            return "other-" + etikett;
        }

        @Cacheable(cacheNames = "pdlCache", keyGenerator = "userkeygenerator")
        public List<String> values(String ident) {
            verdiKall.incrementAndGet();
            return List.of(etikett, ident);
        }

        @Cacheable(cacheNames = "pdlCache", keyGenerator = "userkeygenerator", unless = "#result == null")
        public String empty(String ident) {
            tomKall.incrementAndGet();
            return null;
        }
    }
}

package no.nav.modiapersonoversikt.infrastructure.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;

import java.time.Duration;
import java.util.Arrays;

@Configuration
@EnableCaching
@EnableAspectJAutoProxy
@ImportResource("classpath*:*cacheconfig.xml")
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
                cache("abacClientCache", 3600, 10_000),
                cache("endpointCache", 3, 10_000),
                cache("kjerneinformasjonCache", 60),
                cache("ytelseskontrakter", 600),
                cache("pleiePengerCache", 300),
                cache("organisasjonCache", 300),
                cache("oppfolgingsinfoCache", 300),
                cache("oppfolgingCache", 600),
                cache("foreldrePengerCache", 300),
                cache("hentSykmeldingsperioderCache", 300),
                cache("utbetalingCache", 1800, 10_000),
                cache("pdlCache", 3600, 100_000),
                cache("asbogosysEnhet", 86400, 10_000),
                cache("asbogosysAnsatt", 14400, 10_000),
                cache("asbogosysAnsattListe", 43200, 10_000),
                cache("ldap", 3600, 20_000),
                cache("varslingCache", 180, 10_000),
                cache("innsynJournalCache", 1800, 10_000),
                cache("pesysCache", 600)
        ));

        return cacheManager;
    }

    private static CaffeineCache cache(String name, int time) {
        return cache(name, time, 1000);
    }

    private static CaffeineCache cache(String name, int time, int maximumSize) {
        Cache<Object, Object> cache = Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofSeconds(time))
                .expireAfterWrite(Duration.ofSeconds(time))
                .maximumSize(maximumSize)
                .build();
        
        return new CaffeineCache(name, cache);
    }
}

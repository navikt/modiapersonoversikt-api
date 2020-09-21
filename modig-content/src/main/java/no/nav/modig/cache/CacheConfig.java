package no.nav.modig.cache;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    private static final PersistenceConfiguration persistence = new PersistenceConfiguration()
            .strategy(PersistenceConfiguration.Strategy.NONE);

    @Bean
    public net.sf.ehcache.CacheManager ehcacheCacheManager() {
        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
        config.setName("cachemanager");

        config.addCache(cache("abacClientCache", 3600).maxEntriesLocalHeap(10_000));
        config.addCache(cache("endpointCache", 3).maxEntriesLocalHeap(10_000));
        config.addCache(cache("kjerneinformasjonCache", 60));
        config.addCache(cache("kodeverk_consumer.kodeverkCache", 3600));
        config.addCache(cache("ytelseskontrakter", 600));
        config.addCache(cache("pleiePengerCache", 300));
        config.addCache(cache("organisasjonCache", 300));
        config.addCache(cache("organisasjonEnhetKontaktinformasjonCache", 300));
        config.addCache(cache("oppfolgingsinfoCache", 300));
        config.addCache(cache("oppfolgingCache", 600));
        config.addCache(cache("foreldrePengerCache", 300));
        config.addCache(cache("hentSykmeldingsperioderCache", 300));
        config.addCache(cache("kodeverkCache", 3600));
        config.addCache(cache("utbetalingCache", 1800).maxEntriesLocalHeap(10_000));
        config.addCache(cache("aktorIdCache", Integer.MAX_VALUE).maxEntriesLocalHeap(100_000));
        config.addCache(cache("asbogosysEnhet", 86400).maxEntriesLocalHeap(10_000));
        config.addCache(cache("asbogosysAnsatt", 14400).maxEntriesLocalHeap(10_000));
        config.addCache(cache("asbogosysAnsattListe", 43200).maxEntriesLocalHeap(10_000));
        config.addCache(cache("organisasjonEnhetV2", 86400).maxEntriesLocalHeap(10_000));
        config.addCache(cache("ldap", 3600).maxEntriesLocalHeap(20_000));
        config.addCache(cache("varslingCache", 180).maxEntriesLocalHeap(10_000));
        config.addCache(cache("kodeverksmapperCache", 86400));
        config.addCache(cache("innsynJournalCache", 1800).maxEntriesLocalHeap(10_000));
        config.addCache(cache("pesysCache", 600));

        return net.sf.ehcache.CacheManager.newInstance(config);
    }

    @Bean
    public CacheManager cacheManager(net.sf.ehcache.CacheManager ehcacheCacheManager) {
        return new EhCacheCacheManager(ehcacheCacheManager);
    }

    private static CacheConfiguration cache(String name, int time) {
        return cache(name, time, time);
    }

    private static CacheConfiguration cache(String name, int tti, int ttl) {
        return new CacheConfiguration(name, 100)
                .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU)
                .timeToIdleSeconds(tti)
                .timeToLiveSeconds(ttl)
                .persistence(persistence)
                .maxEntriesLocalHeap(1000);
    }
}

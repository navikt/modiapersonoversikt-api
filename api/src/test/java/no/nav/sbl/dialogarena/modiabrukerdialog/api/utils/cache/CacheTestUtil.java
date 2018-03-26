package no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.cache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;

import java.util.List;

public class CacheTestUtil {

    public static final String CACHEMANAGER = "cachemanager";

    public static void setupCache(List<String> cacheNames ) {
        Configuration cacheManagerConfiguration = new Configuration().name(CACHEMANAGER);
        for (String cacheName: cacheNames) {
            cacheManagerConfiguration.addCache(new CacheConfiguration(cacheName, 100));
        }
        CacheManager.create(cacheManagerConfiguration);
    }

    public static void tearDown() {
        CacheManager.getCacheManager(CACHEMANAGER).shutdown();
    }

}


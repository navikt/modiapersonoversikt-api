package no.nav.modig.cache;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public EhCacheCacheManager cacheManager() {
        EhCacheCacheManager cacheManager = new EhCacheCacheManager();
        net.sf.ehcache.CacheManager manager = ehCacheManagerFactoryBean().getObject();
        cacheManager.setCacheManager(manager);
        return cacheManager;
    }

    @Bean
    public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        ehCacheManagerFactoryBean.setCacheManagerName("cachemanager");
        return ehCacheManagerFactoryBean;
    }
}

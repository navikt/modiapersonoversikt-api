package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config;

import no.nav.modig.core.exception.SystemException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class TestBeans {
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public CacheManager cacheManager() {
        EhCacheCacheManager ehCacheCacheManager = new EhCacheCacheManager();
        try {
            ehCacheCacheManager.setCacheManager(ehCacheCacheManager().getObject());
        } catch (Exception e) {
            throw new SystemException("Feil ved opprettelse av EhCacheManager", e);
        }
        return ehCacheCacheManager;
    }

    @Bean
    public FactoryBean<net.sf.ehcache.CacheManager> ehCacheCacheManager() {
        EhCacheManagerFactoryBean factoryBean = new EhCacheManagerFactoryBean();
        factoryBean.setConfigLocation(new ClassPathResource("ehcache_test.xml"));
        return factoryBean;
    }


}

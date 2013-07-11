package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.modig.core.exception.SystemException;
import org.slf4j.Logger;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import static org.slf4j.LoggerFactory.getLogger;

@Configuration
@EnableCaching
public class CacheConfig {

    private Logger logger = getLogger(CacheConfig.class);

    @Bean
    public CacheManager cacheManager() {
        EhCacheCacheManager ehCacheCacheManager = new EhCacheCacheManager();
        try {
            ehCacheCacheManager.setCacheManager(ehCacheCacheManager().getObject());
        } catch (Exception e) {
            logger.error("Feil ved opprettelse av EhCacheManager", e.getMessage());
            throw new SystemException("Feil ved opprettelse av EhCacheManager", e);
        }
        return ehCacheCacheManager;
    }

    @Bean
    public FactoryBean<net.sf.ehcache.CacheManager> ehCacheCacheManager() {
        EhCacheManagerFactoryBean factoryBean = new EhCacheManagerFactoryBean();
        factoryBean.setConfigLocation( new ClassPathResource("ehcache.xml") );
        return factoryBean;
    }

}

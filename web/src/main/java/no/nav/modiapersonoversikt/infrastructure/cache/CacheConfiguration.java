package no.nav.modiapersonoversikt.infrastructure.cache;

import no.nav.modiapersonoversikt.infrastructure.cache.CacheConfig;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;

@Configuration
@EnableCaching
@EnableAspectJAutoProxy
@ImportResource("classpath*:*cacheconfig.xml")
public class CacheConfiguration extends CacheConfig {

}

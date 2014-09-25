package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.cache;

import no.nav.modig.cache.CacheConfig;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
//@ImportResource("classpath*:*cacheconfig.xml")
public class CacheConfiguration extends CacheConfig {
}

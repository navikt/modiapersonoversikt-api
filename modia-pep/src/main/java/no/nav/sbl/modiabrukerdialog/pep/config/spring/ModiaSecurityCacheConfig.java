package no.nav.sbl.modiabrukerdialog.pep.config.spring;

import net.sf.ehcache.CacheManager;
import no.nav.modig.security.tilgangskontroll.config.SecurityCacheConfig;
import no.nav.modig.security.tilgangskontroll.policy.pip.cache.Cache;
import no.nav.modig.security.tilgangskontroll.policy.pip.cache.EhCache;
import org.jboss.security.xacml.sunxacml.cond.EvaluationResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;


/**
 * no.nav.modig.security.tilgangskontroll.config.SecurityCacheConfig oppretter sin egen CacheManager og skaper problemer
 * hvis det allerede har blitt instansiert en CacheManager.
 */
@Configuration
public class ModiaSecurityCacheConfig extends SecurityCacheConfig{

	@Inject
	private CacheManager cacheManager;

	@Bean
	@Override
	public Cache<String, EvaluationResult> cache() {
		EhCache<String, EvaluationResult> cache = new EhCache<>();
		cache.setCacheManager(cacheManager);

		return cache;
	}
}

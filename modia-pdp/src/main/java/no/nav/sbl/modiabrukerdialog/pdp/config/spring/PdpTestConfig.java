package no.nav.sbl.modiabrukerdialog.pdp.config.spring;

import no.nav.modig.security.tilgangskontroll.config.AccessControlInterceptorConfig;
import no.nav.modig.security.tilgangskontroll.config.SecurityCacheConfig;
import no.nav.modig.security.tilgangskontroll.policy.pdp.DecisionPoint;
import no.nav.modig.security.tilgangskontroll.policy.pdp.picketlink.PicketLinkDecisionPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.net.URL;

@Configuration
@Import(value = { AccessControlInterceptorConfig.class, SecurityCacheConfig.class })
public class PdpTestConfig {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/*
	 * PDP (Policy Decision Point) inneholder regelsett for tilgang, og avgjør hvorvidt bruker får tilgang. I første omgang vil
	 * PDP være en integrert del av applikasjonen, men det er mulig at PDP vil trekkes ut som en tjeneste senere.
	 */
	@Bean
	public DecisionPoint modiaPdp() {
		return new PicketLinkDecisionPoint(getConfigUrl("config/modia-policy-config-test.xml"));
	}

	protected URL getConfigUrl(String path) {
		try {
			return new ClassPathResource(path).getURL();
		} catch (IOException e) {
			logger.error("IOException i SecurityPolicyTestConfig", e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
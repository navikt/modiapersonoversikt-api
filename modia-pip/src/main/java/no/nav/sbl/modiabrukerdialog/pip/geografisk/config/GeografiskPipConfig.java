package no.nav.sbl.modiabrukerdialog.pip.geografisk.config;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navorgenhet.GOSYSNAVOrgEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v1.norg.NAVAnsattEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v1.norg.NAVOrgEnhetEndpointConfig;
import no.nav.sbl.modiabrukerdialog.pip.geografisk.support.DefaultEnhetAttributeLocatorDelegate;
import no.nav.sbl.modiabrukerdialog.pip.geografisk.support.EnhetAttributeLocatorDelegate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

/**
 * Spring configuration for NAVOrgEnhet and NAVAnsatt.
 */
@Configuration
@Import(value = {NAVAnsattEndpointConfig.class, NAVOrgEnhetEndpointConfig.class})
public class GeografiskPipConfig {

	public static final String TJENESTEBUSS_URL_KEY = "tjenestebuss.url";
	public static final String TJENESTEBUSS_USERNAME_KEY = "ctjenestebuss.username";
	public static final String TJENESTEBUSS_PASSWORD_KEY = "ctjenestebuss.password";

	@Inject
	private GOSYSNAVansatt ansattService;
	@Inject
	private GOSYSNAVOrgEnhet enhetService;

	@Bean
	public EnhetAttributeLocatorDelegate enhetAttributeLocatorDelegate() {
		return new DefaultEnhetAttributeLocatorDelegate(ansattService, enhetService);
	}
}


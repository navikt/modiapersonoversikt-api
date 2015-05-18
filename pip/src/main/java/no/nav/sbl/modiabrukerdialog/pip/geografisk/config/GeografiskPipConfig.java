package no.nav.sbl.modiabrukerdialog.pip.geografisk.config;

import no.nav.sbl.modiabrukerdialog.pip.geografisk.support.DefaultEnhetAttributeLocatorDelegate;
import no.nav.sbl.modiabrukerdialog.pip.geografisk.support.EnhetAttributeLocatorDelegate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for NAVOrgEnhet and NAVAnsatt.
 */
@Configuration
public class GeografiskPipConfig {

    public static final String TJENESTEBUSS_URL_KEY = "tjenestebuss.url";
    public static final String TJENESTEBUSS_USERNAME_KEY = "ctjenestebuss.username";
    public static final String TJENESTEBUSS_PASSWORD_KEY = "ctjenestebuss.password";

    @Bean
    public EnhetAttributeLocatorDelegate enhetAttributeLocatorDelegate() {
        return new DefaultEnhetAttributeLocatorDelegate();
    }
}


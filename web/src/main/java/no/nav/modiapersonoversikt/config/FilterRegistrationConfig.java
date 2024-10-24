package no.nav.modiapersonoversikt.config;

import no.nav.common.auth.context.UserRole;
import no.nav.common.auth.oidc.filter.OidcAuthenticationFilter;
import no.nav.common.auth.oidc.filter.OidcAuthenticator;
import no.nav.common.auth.oidc.filter.OidcAuthenticatorConfig;
import no.nav.common.rest.filter.LogRequestFilter;
import no.nav.common.rest.filter.SetStandardHttpHeadersFilter;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.personoversikt.common.science.scientist.ScientistFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

import static no.nav.common.utils.EnvironmentUtils.isDevelopment;

@Configuration
@Profile("!local")
public class FilterRegistrationConfig {
    private static final String azureAdClientId = EnvironmentUtils.getRequiredProperty("AZURE_APP_CLIENT_ID");
    private static final String azureAdDiscoveryUrl = EnvironmentUtils.getRequiredProperty("AZURE_APP_WELL_KNOWN_URL");

    @Bean
    public OidcAuthenticator authConfig() {
        OidcAuthenticatorConfig azureAdConfig = new OidcAuthenticatorConfig()
                .withClientId(azureAdClientId)
                .withDiscoveryUrl(azureAdDiscoveryUrl)
                .withUserRole(UserRole.INTERN);

        return OidcAuthenticator.fromConfig(azureAdConfig);
    }

    @Bean
    public FilterRegistrationBean<OidcAuthenticationFilter> authenticationFilterRegistration(List<OidcAuthenticator> authenticators) {
        FilterRegistrationBean<OidcAuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new OidcAuthenticationFilter(authenticators));
        registration.setOrder(1);
        registration.addUrlPatterns("/rest/*");
        registration.addUrlPatterns("/internal/aaputsending/*");
        return registration;
    }

    @Bean
    public FilterRegistrationBean<LogRequestFilter> logFilterRegistrationBean() {
        FilterRegistrationBean<LogRequestFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new LogRequestFilter("modiapersonoversikt-api", isDevelopment().orElse(false)));
        registration.setOrder(2);
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    public FilterRegistrationBean<SetStandardHttpHeadersFilter> setStandardHeadersFilterRegistrationBean() {
        FilterRegistrationBean<SetStandardHttpHeadersFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new SetStandardHttpHeadersFilter());
        registration.setOrder(3);
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    public FilterRegistrationBean<ScientistFilter> scientistFilterRegistrationBean() {
        FilterRegistrationBean<ScientistFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ScientistFilter());
        registration.setOrder(4);
        registration.addUrlPatterns("/rest/*");
        return registration;
    }
}

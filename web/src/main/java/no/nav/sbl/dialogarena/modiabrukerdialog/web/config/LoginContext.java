package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.common.auth.Constants;
import no.nav.common.auth.oidc.filter.OidcAuthenticationFilter;
import no.nav.common.auth.oidc.filter.OidcAuthenticator;
import no.nav.common.auth.oidc.filter.OidcAuthenticatorConfig;
import no.nav.common.auth.subject.IdentType;
import no.nav.common.log.LogFilter;
import no.nav.common.rest.filter.SetStandardHttpHeadersFilter;
import no.nav.common.utils.EnvironmentUtils;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static no.nav.common.utils.EnvironmentUtils.isDevelopment;

@Configuration
public class LoginContext {
    private static final String issoClientId = EnvironmentUtils.getRequiredProperty("ISSO_CLIENT_ID");
    private static final String issoDiscoveryUrl = EnvironmentUtils.getRequiredProperty("ISSO_DISCOVERY_URL");
    private static final String issoRefreshUrl = EnvironmentUtils.getRequiredProperty("ISSO_REFRESH_URL");
    private static final String fpsakClientId = EnvironmentUtils.getRequiredProperty("FPSAK_CLIENT_ID");

    @Bean
    public OidcAuthenticator openAmAuthConfig() {
        OidcAuthenticatorConfig config = new OidcAuthenticatorConfig()
                .withClientId(issoClientId)
                .withDiscoveryUrl(issoDiscoveryUrl)
                .withIdTokenCookieName(Constants.OPEN_AM_ID_TOKEN_COOKIE_NAME)
                .withIdentType(IdentType.InternBruker)
                .withRefreshUrl(issoRefreshUrl)
                .withRefreshTokenCookieName(Constants.REFRESH_TOKEN_COOKIE_NAME);

        return OidcAuthenticator.fromConfig(config);
    }

    @Bean
    public OidcAuthenticator fpsakAuthConfig() {
        OidcAuthenticatorConfig config = new OidcAuthenticatorConfig()
                .withClientId(fpsakClientId)
                .withDiscoveryUrl(issoDiscoveryUrl)
                .withIdTokenCookieName(Constants.OPEN_AM_ID_TOKEN_COOKIE_NAME)
                .withIdentType(IdentType.InternBruker);

        return OidcAuthenticator.fromConfig(config);
    }

    @Bean
    public FilterRegistrationBean authenticationFilterRegistration(List<OidcAuthenticator> authenticators) {
        FilterRegistrationBean<OidcAuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new OidcAuthenticationFilter(authenticators));
        registration.setOrder(1);
        registration.addUrlPatterns("/rest/*");
        registration.addUrlPatterns("/internal/aaputsending/*");
        return registration;
    }

    @Bean
    public FilterRegistrationBean logFilterRegistrationBean() {
        FilterRegistrationBean<LogFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new LogFilter("modiapersonoversikt-api", isDevelopment().orElse(false)));
        registration.setOrder(2);
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    public FilterRegistrationBean setStandardHeadersFilterRegistrationBean() {
        FilterRegistrationBean<SetStandardHttpHeadersFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new SetStandardHttpHeadersFilter());
        registration.setOrder(3);
        registration.addUrlPatterns("/*");
        return registration;
    }

}

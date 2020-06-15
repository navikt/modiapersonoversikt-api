package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.apiapp.ApiApplication;
import no.nav.apiapp.config.ApiAppConfigurator;
import no.nav.brukerdialog.security.domain.IdentType;
import no.nav.common.oidc.Constants;
import no.nav.common.oidc.auth.OidcAuthenticatorConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.RedirectFilter;
import no.nav.sbl.util.EnvironmentUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.servlet.ServletContext;

@Configuration
@Import({
        ApplicationContextBeans.class,
        ModulesApplicationContext.class,
        RestApiBeans.class
})

public class ModiaApplicationContext implements ApiApplication {
    private static final String issoClientId = EnvironmentUtils.getRequiredProperty("ISSO_CLIENT_ID");
    private static final String issoDiscoveryUrl = EnvironmentUtils.getRequiredProperty("ISSO_DISCOVERY_URL");
    private static final String issoRefreshUrl = EnvironmentUtils.getRequiredProperty("ISSO_REFRESH_URL");
    private static final String fpsakClientId = EnvironmentUtils.getRequiredProperty("FPSAK_CLIENT_ID");

    @Override
    public void startup(ServletContext servletContext) {
        JmxExporterConfig.setup();
    }

    @Override
    public void configure(ApiAppConfigurator apiAppConfigurator) {
        OidcAuthenticatorConfig isso = new OidcAuthenticatorConfig()
                .withClientId(issoClientId)
                .withDiscoveryUrl(issoDiscoveryUrl)
                .withIdTokenCookieName(Constants.OPEN_AM_ID_TOKEN_COOKIE_NAME)
                .withIdentType(IdentType.InternBruker)
                .withRefreshUrl(issoRefreshUrl)
                .withRefreshTokenCookieName(Constants.REFRESH_TOKEN_COOKIE_NAME);

        OidcAuthenticatorConfig fpsak = new OidcAuthenticatorConfig()
                .withClientId(fpsakClientId)
                .withDiscoveryUrl(issoDiscoveryUrl)
                .withIdTokenCookieName(Constants.OPEN_AM_ID_TOKEN_COOKIE_NAME)
                .withIdentType(IdentType.InternBruker)
                .withRefreshUrl(issoRefreshUrl)
                .withRefreshTokenCookieName(Constants.REFRESH_TOKEN_COOKIE_NAME);

        apiAppConfigurator
                .enableCXFSecureLogs()
                .sts()
                .objectMapper(JacksonConfig.mapper)
                .addOidcAuthenticator(isso)
                .addOidcAuthenticator(fpsak)
                .customizeJettyBuilder(jetty -> {
                    // Filteret må ligge slik at det havner etter LoginFilter.
                    // Alternativet er å legge det i `startup`-metoden (override) men da havner det etter LoginFilter
                    // Og da har man ikke mulighet til å hente ut Subject som er nødvendig for at unleash skal fungere.
                    jetty.addFilter(new RedirectFilter());
                    jetty.at("modiapersonoversikt-api");
                });
    }

    @Override
    public String getApiBasePath() {
        return "/rest/";
    }
}

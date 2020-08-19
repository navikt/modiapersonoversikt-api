package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.common.auth.Constants;
import no.nav.common.auth.oidc.filter.OidcAuthenticatorConfig;
import no.nav.common.auth.subject.IdentType;
import no.nav.common.utils.EnvironmentUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.servlet.ServletContext;

@Configuration
@Import({
        ApplicationContextBeans.class,
        ModulesApplicationContext.class,
        RestApiBeans.class
})

public class ModiaApplicationContext {
    private static final String issoClientId = EnvironmentUtils.getRequiredProperty("ISSO_CLIENT_ID");
    private static final String issoDiscoveryUrl = EnvironmentUtils.getRequiredProperty("ISSO_DISCOVERY_URL");
    private static final String issoRefreshUrl = EnvironmentUtils.getRequiredProperty("ISSO_REFRESH_URL");
    private static final String fpsakClientId = EnvironmentUtils.getRequiredProperty("FPSAK_CLIENT_ID");

    public void startup(ServletContext servletContext) {
        JmxExporterConfig.setup();
    }

    public void configure() {
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

//        apiAppConfigurator
//                .enableCXFSecureLogs()
//                .sts()
//                .objectMapper(JacksonConfig.mapper)
//                .addOidcAuthenticator(isso)
//                .addOidcAuthenticator(fpsak)
//                .customizeJettyBuilder(jetty -> jetty.at("modiapersonoversikt-api"));
    }

    public String getApiBasePath() {
        return "/rest/";
    }
}

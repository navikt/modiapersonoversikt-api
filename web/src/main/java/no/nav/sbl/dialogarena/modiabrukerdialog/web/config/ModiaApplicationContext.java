package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.apiapp.ApiApplication;
import no.nav.apiapp.config.ApiAppConfigurator;
import no.nav.brukerdialog.security.Constants;
import no.nav.brukerdialog.security.domain.IdentType;
import no.nav.common.oidc.auth.OidcAuthenticatorConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.RedirectFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        ApplicationContextBeans.class,
        ModulesApplicationContext.class,
        RestApiBeans.class
})
public class ModiaApplicationContext implements ApiApplication {
    @Override
    public void configure(ApiAppConfigurator apiAppConfigurator) {
        OidcAuthenticatorConfig isso = new OidcAuthenticatorConfig()
                .withClientId("veilarblogin-q6")
                .withDiscoveryUrl("https://isso-q.adeo.no/isso/oauth2/.well-known/openid-configuration")
                .withIdTokenCookieName(Constants.ID_TOKEN_COOKIE_NAME)
                .withIdentType(IdentType.InternBruker)
                .withRefreshUrl("https://app-q6.adeo.no/veilarblogin/api/openam-refresh")
                .withRefreshTokenCookieName(Constants.REFRESH_TOKEN_COOKIE_NAME);

        apiAppConfigurator
                .customizeJettyBuilder(jetty -> {
                    // Filteret må ligge slik at det havner etter LoginFilter.
                    // Alternativet er å legge det i `startup`-metoden (override) men da havner det etter LoginFilter
                    // Og da har man ikke mulighet til å hente ut Subject som er nødvendig for at unleash skal fungere.
                    jetty.addFilter(new RedirectFilter());
                    jetty.at("modiapersonoversikt-api");
                })
                .sts()
                .objectMapper(JacksonConfig.mapper)
                .addOidcAuthenticator(isso);
    }

    @Override
    public String getApiBasePath() {
        return "/rest/";
    }
}

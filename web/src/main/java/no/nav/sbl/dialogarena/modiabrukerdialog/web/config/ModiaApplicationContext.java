package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.apiapp.ApiApplication;
import no.nav.apiapp.config.ApiAppConfigurator;
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
                .issoLogin();
    }

    @Override
    public String getApiBasePath() {
        return "/rest/";
    }
}

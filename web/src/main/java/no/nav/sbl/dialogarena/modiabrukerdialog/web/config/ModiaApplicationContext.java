package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.apiapp.ApiApplication;
import no.nav.apiapp.config.ApiAppConfigurator;
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
                .customizeJettyBuilder(jetty -> jetty.at("modiapersonoversikt-api"))
                .sts()
                .issoLogin();
    }

    @Override
    public String getApiBasePath() {
        return "/rest/";
    }
}

package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.apiapp.ApiApplication;
import no.nav.apiapp.config.ApiAppConfigurator;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        WicketApplicationBeans.class,
        ApplicationContextBeans.class,
        ModulesApplicationContext.class,
        RestApiBeans.class,
        SessionConfig.class
})
public class ModiaApplicationContext implements ApiApplication.NaisApiApplication {

    @Override
    public void configure(ApiAppConfigurator apiAppConfigurator) {
        apiAppConfigurator
                .sts()
                .issoLogin();
    }

    @Override
    public String getApiBasePath() {
        return "/rest/";
    }

    @Override
    public String getApplicationName() {
        return "modiabrukerdialog";
    }

    @Override
    public Sone getSone() {
        return Sone.FSS;
    }

}

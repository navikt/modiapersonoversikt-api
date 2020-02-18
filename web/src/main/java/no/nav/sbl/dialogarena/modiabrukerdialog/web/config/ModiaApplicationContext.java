package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.apiapp.ApiApplication;
import no.nav.apiapp.config.ApiAppConfigurator;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.RedirectFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import javax.servlet.ServletContext;

import static no.nav.apiapp.ServletUtil.filterBuilder;

@Configuration
@Import({
        ApplicationContextBeans.class,
        ModulesApplicationContext.class,
        RestApiBeans.class
})
public class ModiaApplicationContext implements ApiApplication {

    @Override
    public void startup(ServletContext servletContext) {
        filterBuilder(RedirectFilter.class).register(servletContext);
    }

    @Override
    public void configure(ApiAppConfigurator apiAppConfigurator) {
        apiAppConfigurator
                .customizeJettyBuilder(jetty -> jetty.at("modiapersonoversikt-api"))
                .sts()
                .objectMapper(JacksonConfig.mapper)
                .enableCXFSecureLogs()
                .issoLogin();
    }

    @Override
    public String getApiBasePath() {
        return "/rest/";
    }
}

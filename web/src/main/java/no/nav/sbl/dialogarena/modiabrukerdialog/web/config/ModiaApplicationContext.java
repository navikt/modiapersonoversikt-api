package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.servlet.ServletContext;

@Configuration
@Import({
        LoginContext.class,
        ApplicationContextBeans.class,
        ModulesApplicationContext.class,
        SelftestContext.class
})

public class ModiaApplicationContext {
    public void startup(ServletContext servletContext) {
        JmxExporterConfig.setup();
    }

    public String getApiBasePath() {
        return "/rest/";
    }
}

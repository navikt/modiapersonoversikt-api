package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;

@Configuration
@Import({
        LoginContext.class,
        ApplicationContextBeans.class,
        ModulesApplicationContext.class,
        SelftestContext.class
})

public class ModiaApplicationContext {
    @PostConstruct
    public void setup() {
        JmxExporterConfig.setup();
    }
}

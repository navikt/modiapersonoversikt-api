package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketApplication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ComponentsContext.class)
public class ApplicationContext {

    @Bean
    public WicketApplication modiaApplication() {
        return new WicketApplication();
    }
}

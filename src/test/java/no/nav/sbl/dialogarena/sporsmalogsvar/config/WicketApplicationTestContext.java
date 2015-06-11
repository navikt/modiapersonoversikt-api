package no.nav.sbl.dialogarena.sporsmalogsvar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WicketApplicationTestContext {

    @Bean
    public InnboksTestApplication innboksTestApplication() {
        return new InnboksTestApplication();
    }

}

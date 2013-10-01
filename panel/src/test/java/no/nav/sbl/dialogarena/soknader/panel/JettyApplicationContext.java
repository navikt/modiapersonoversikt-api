package no.nav.sbl.dialogarena.soknader.panel;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JettyApplicationContext {

    @Bean
    public SoeknaderTestApplication application() {
        return new SoeknaderTestApplication();
    }

}

package no.nav.sbl.dialogarena.besvare;

import no.nav.sbl.dialogarena.besvare.config.BesvareSporsmalConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(BesvareSporsmalConfig.class)
public class JettyApplicationContext {

    @Bean
    public BesvareSporsmalApplication application() {
        return new BesvareSporsmalApplication();
    }

}

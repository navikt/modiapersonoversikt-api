package no.nav.sbl.dialogarena.sak.config;

import no.nav.sbl.dialogarena.sak.service.SaksoversiktService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import({
        KodeverkConfig.class
})
public class SaksoversiktServiceConfig {

    @Bean
    public SaksoversiktService saksoversiktService() {
        return new SaksoversiktService();
    }

}

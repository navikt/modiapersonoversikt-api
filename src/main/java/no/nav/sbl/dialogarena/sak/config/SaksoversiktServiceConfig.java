package no.nav.sbl.dialogarena.sak.config;

import no.nav.sbl.dialogarena.sak.service.*;
import no.nav.sbl.dialogarena.sak.service.enonic.MiljovariablerService;
import no.nav.sbl.dialogarena.sak.service.interfaces.*;

import no.nav.sbl.dialogarena.saksoversikt.service.config.ServiceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({KodeverkConfig.class, EnonicConfig.class, ServiceConfig.class})
public class SaksoversiktServiceConfig {

    @Bean
    public SaksoversiktService saksoversiktService() {
        return new SaksoversiktServiceImpl();
    }

    @Bean
    public TilgangskontrollService tilgangskontrollService() {
        return new TilgangskontrollServiceImpl();
    }

    @Bean
    public DataFletter dataFletter() {
        return new DataFletterImpl();
    }

    @Bean
    public MiljovariablerService miljovariablerService() {
        return new MiljovariablerService();
    }

}

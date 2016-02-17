package no.nav.sbl.dialogarena.sak.config;

import no.nav.sbl.dialogarena.sak.service.SaksoversiktServiceImpl;
import no.nav.sbl.dialogarena.sak.service.TilgangskontrollServiceImpl;
import no.nav.sbl.dialogarena.sak.service.enonic.MiljovariablerService;
import no.nav.sbl.dialogarena.sak.service.interfaces.SaksoversiktService;
import no.nav.sbl.dialogarena.sak.service.interfaces.TilgangskontrollService;
import no.nav.sbl.dialogarena.sak.transformers.FilterImpl;
import no.nav.sbl.dialogarena.saksoversikt.service.config.ServiceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({EnonicConfig.class, ServiceConfig.class})
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
    public MiljovariablerService miljovariablerService() {
        return new MiljovariablerService();
    }

    @Bean
    public FilterImpl filter() {
        return new FilterImpl();
    }

}

package no.nav.sbl.dialogarena.sak.config;

import no.nav.sbl.dialogarena.sak.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({KodeverkConfig.class})
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
    public BulletproofCmsService bulletproofCmsService() {
        return new BulletproofCmsServiceImpl();
    }

    @Bean
    public Filter sakOgBehandlingFilter() {
        return new FilterImpl();
    }

    @Bean
    public SakOgBehandlingService sakOgBehandlingService() {
        return new SakOgBehandlingServiceImpl();
    }

    @Bean
    public HenvendelseService henvendelseService() {
        return new HenvendelseServiceImpl();
    }

    @Bean
    public GSakService gSakService() {
        return new GSakServiceImpl();
    }

    @Bean
    public JoarkService joarkService() {
        return new JoarkServiceImpl();
    }


    @Bean
    public DataFletter dataFletter() {
        return new DataFletterImpl();
    }

}

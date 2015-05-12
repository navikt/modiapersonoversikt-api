package no.nav.sbl.dialogarena.sak.config;

import no.nav.sbl.dialogarena.sak.service.*;
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

    @Bean
    public TilgangskontrollService tilgangskontrollService() {
        return new TilgangskontrollService();
    }

    @Bean
    public BulletproofCmsService bulletproofCmsService() {
        return new BulletproofCmsService();
    }

    @Bean
    public Filter sakOgBehandlingFilter() {
        return new Filter();
    }

    @Bean
    public SakOgBehandlingService sakOgBehandlingService() {
        return new SakOgBehandlingService();
    }

    @Bean
    public HenvendelseService henvendelseService() {
        return new HenvendelseService();
    }

    @Bean
    public GSakService gSakService() {
        return new GSakService();
    }

    @Bean
    public DataFletter dataFletter() {
        return new DataFletter();
    }

}

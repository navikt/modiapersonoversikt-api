package no.nav.sbl.dialogarena.sak.config;

import no.nav.sbl.dialogarena.sak.service.BulletproofCmsService;
import no.nav.sbl.dialogarena.sak.service.DataFletter;
import no.nav.sbl.dialogarena.sak.service.HenvendelseService;
import no.nav.sbl.dialogarena.sak.service.SakOgBehandlingFilter;
import no.nav.sbl.dialogarena.sak.service.SakOgBehandlingService;
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

    @Bean
    public BulletproofCmsService bulletproofCmsService() {
        return new BulletproofCmsService();
    }

    @Bean
    public SakOgBehandlingFilter sakOgBehandlingFilter() {
        return new SakOgBehandlingFilter();
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
    public DataFletter dataFletter() {
        return new DataFletter();
    }

}

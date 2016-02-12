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
    public InnsynJournalService joarkService() {
        return new InnsynJournalServiceImpl();
    }

    @Bean
    public DataFletter dataFletter() {
        return new DataFletterImpl();
    }

    @Bean
    public SaksService saksService() {
        return new SaksServiceImpl();
    }

    @Bean
    public PesysService pesysService() {
        return new PesysServiceImpl();
    }

    @Bean
    public GsakSakerService gsakSakerService() {
        return new GsakSakerService();
    }

}

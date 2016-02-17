package no.nav.sbl.dialogarena.saksoversikt.service.config;

import no.nav.sbl.dialogarena.saksoversikt.service.service.*;
import no.nav.sbl.dialogarena.saksoversikt.service.utils.TemagrupperHenter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Configuration
public class ServiceConfig {

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
    public GsakSakerService gSakService() {
        return new GsakSakerService();
    }

    @Bean
    public InnsynJournalService joarkService() {
        return new InnsynJournalService();
    }

    @Bean
    public PesysService pesysService() {
        return new PesysService();
    }

    @Bean
    public SaksService saksService() {
        return new SaksService();
    }

    @Bean
    public GsakSakerService gsakSakerService() {
        return new GsakSakerService();
    }

    @Bean
    public FodselnummerAktorService fodselnummerAktorService() {
        return new FodselnummerAktorService();
    }

    @Bean
    public DataFletter dataFletter() {
        return new DataFletter();
    }

    @Bean
    public DokumentMetadataService dokumentMetadataService() {
        return new DokumentMetadataService();
    }

    @Bean
    public TemagrupperHenter temagrupperHenter() {
        return new TemagrupperHenter();
    }

    @Bean
    public SakstemaGrupperer sakstemaGrupperer() {
        return new SakstemaGrupperer();
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(50);
    }
//
//    @Bean
//    public BulletproofKodeverkService kodeverkService() {
//        return new BulletproofKodeverkService();
//    }
}



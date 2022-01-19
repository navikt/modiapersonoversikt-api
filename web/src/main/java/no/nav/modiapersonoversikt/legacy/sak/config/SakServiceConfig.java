package no.nav.modiapersonoversikt.legacy.sak.config;

import no.nav.modiapersonoversikt.config.service.EnhetligKodeverkConfig;
import no.nav.modiapersonoversikt.legacy.sak.service.*;
import no.nav.modiapersonoversikt.legacy.sak.service.filter.Filter;
import no.nav.modiapersonoversikt.legacy.sak.service.saf.ExperimentSafService;
import no.nav.modiapersonoversikt.legacy.sak.service.saf.SafGraphqlServiceImpl;
import no.nav.modiapersonoversikt.legacy.sak.service.saf.SafService;
import no.nav.modiapersonoversikt.legacy.sak.service.saf.SafServiceImpl;
import no.nav.modiapersonoversikt.legacy.sak.utils.TemagrupperHenter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({EnhetligKodeverkConfig.class})
public class SakServiceConfig {

    @Bean
    public Filter sakOgBehandlingFilter() {
        return new Filter();
    }

    @Bean
    public SakOgBehandlingService sakOgBehandlingService() {
        return new SakOgBehandlingService();
    }

    @Bean
    public PesysService pesysService() {
        return new PesysService();
    }

    @Bean
    public SakstemaService sakstemaService() {
        return new SakstemaService();
    }

    @Bean
    public SaksService saksService() {
        return new SaksService();
    }

    @Bean
    public DokumentMetadataService dokumentMetadataService(SafService safService) {
        return new DokumentMetadataService(safService);
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
    public BulletproofKodeverkService bulletproofKodeverkService() {
        return new BulletproofKodeverkService();
    }

    @Bean
    public SafService safService() {
        return new ExperimentSafService(
                new SafServiceImpl(),
                new SafGraphqlServiceImpl()
        );
    }
}



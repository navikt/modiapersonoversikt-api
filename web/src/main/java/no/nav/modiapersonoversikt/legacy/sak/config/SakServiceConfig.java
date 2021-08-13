package no.nav.modiapersonoversikt.legacy.sak.config;

import no.nav.modiapersonoversikt.consumer.kodeverk2.config.KodeverkConfig;
import no.nav.modiapersonoversikt.legacy.sak.service.*;
import no.nav.modiapersonoversikt.legacy.sak.service.filter.Filter;
import no.nav.modiapersonoversikt.legacy.sak.service.interfaces.InnsynJournalV2Service;
import no.nav.modiapersonoversikt.legacy.sak.service.saf.ExperimentSafService;
import no.nav.modiapersonoversikt.legacy.sak.service.saf.SafGraphqlServiceImpl;
import no.nav.modiapersonoversikt.legacy.sak.service.saf.SafService;
import no.nav.modiapersonoversikt.legacy.sak.service.saf.SafServiceImpl;
import no.nav.modiapersonoversikt.legacy.sak.transformers.DokumentMetadataTransformer;
import no.nav.modiapersonoversikt.legacy.sak.utils.TemagrupperHenter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({KodeverkConfig.class})
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
    public HenvendelseService henvendelseService() {
        return new HenvendelseService();
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
    public DokumentMetadataService dokumentMetadataService(InnsynJournalV2Service innsynJournalV2Service,
                                                           HenvendelseService henvendelseService,
                                                           DokumentMetadataTransformer dokumentMetadataTransformer,
                                                           SafService safService) {
        return new DokumentMetadataService(innsynJournalV2Service, henvendelseService, dokumentMetadataTransformer, safService);
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
    public DokumentMetadataTransformer dokumentMetadataTransformer(BulletproofKodeverkService bulletproofKodeverkService) {
        return new DokumentMetadataTransformer(bulletproofKodeverkService);
    }

    @Bean
    public SafService safService() {
        return new ExperimentSafService(
                new SafServiceImpl(),
                new SafGraphqlServiceImpl()
        );
    }
}

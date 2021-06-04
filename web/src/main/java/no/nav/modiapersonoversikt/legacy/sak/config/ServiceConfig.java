package no.nav.modiapersonoversikt.legacy.sak.config;

import no.nav.modiapersonoversikt.integration.kodeverk2.JsonKodeverk;
import no.nav.modiapersonoversikt.integration.kodeverk2.Kodeverk;
import no.nav.modiapersonoversikt.legacy.sak.service.*;
import no.nav.modiapersonoversikt.legacy.sak.service.filter.Filter;
import no.nav.modiapersonoversikt.legacy.sak.service.interfaces.InnsynJournalV2Service;
import no.nav.modiapersonoversikt.legacy.sak.service.saf.SafService;
import no.nav.modiapersonoversikt.legacy.sak.transformers.DokumentMetadataTransformer;
import no.nav.modiapersonoversikt.legacy.sak.utils.TemagrupperHenter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public Kodeverk kodeverk() {
        return new JsonKodeverk(getClass().getResourceAsStream("/kodeverk.json"));
    }

    @Bean
    public DokumentMetadataTransformer dokumentMetadataTransformer(BulletproofKodeverkService bulletproofKodeverkService) {
        return new DokumentMetadataTransformer(bulletproofKodeverkService);
    }

    @Bean
    public SafService safService() {
        return new SafService();
    }

}



package no.nav.sbl.dialogarena.modiabrukerdialog.sak.config;

import no.nav.sbl.dialogarena.common.kodeverk.JsonKodeverk;
import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.*;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.filter.Filter;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces.InnsynJournalV2Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.saf.SafService;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.transformers.DokumentMetadataTransformer;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.utils.TemagrupperHenter;
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
    public GsakSakerService gSakService() {
        return new GsakSakerService();
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
    public GsakSakerService gsakSakerService() {
        return new GsakSakerService();
    }

    @Bean
    public FodselnummerAktorService fodselnummerAktorService() {
        return new FodselnummerAktorService();
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



package no.nav.sbl.dialogarena.sak.config;

import no.nav.sbl.dialogarena.sak.service.InnsynJournalV2ServiceImpl;
import no.nav.sbl.dialogarena.sak.service.JournalV2ServiceImpl;
import no.nav.sbl.dialogarena.sak.service.SaksoversiktServiceImpl;
import no.nav.sbl.dialogarena.sak.service.TilgangskontrollServiceImpl;
import no.nav.sbl.dialogarena.sak.service.enonic.MiljovariablerService;
import no.nav.sbl.dialogarena.sak.service.interfaces.SaksoversiktService;
import no.nav.sbl.dialogarena.sak.service.interfaces.TilgangskontrollService;
import no.nav.sbl.dialogarena.sak.transformers.JournalpostTransformer;
import no.nav.sbl.dialogarena.saksoversikt.service.config.ServiceConfig;
import no.nav.sbl.dialogarena.saksoversikt.service.service.interfaces.InnsynJournalV2Service;
import no.nav.sbl.dialogarena.saksoversikt.service.service.interfaces.JournalV2Service;
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
    public JournalV2Service journalV2Service() {
        return new JournalV2ServiceImpl();
    }

    @Bean
    public InnsynJournalV2Service innsynJournalV2Service(){
        return new InnsynJournalV2ServiceImpl();
    }

    @Bean
    public JournalpostTransformer journalpostTransformer() {
        return new JournalpostTransformer();
    }

}

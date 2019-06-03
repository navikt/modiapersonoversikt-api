package no.nav.sbl.dialogarena.modiabrukerdialog.sak.config;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.InnsynJournalV2ServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.JournalV2ServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.SaksoversiktServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.TilgangskontrollServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.enonic.MiljovariablerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces.InnsynJournalV2Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces.JournalV2Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces.SaksoversiktService;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces.TilgangskontrollService;
import no.nav.tjeneste.virksomhet.innsynjournal.v2.binding.InnsynJournalV2;
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
    public InnsynJournalV2Service innsynJournalV2Service(InnsynJournalV2 innsynJournalV2){
        return new InnsynJournalV2ServiceImpl(innsynJournalV2);
    }

}

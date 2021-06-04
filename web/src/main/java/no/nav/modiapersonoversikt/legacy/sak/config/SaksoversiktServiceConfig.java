package no.nav.modiapersonoversikt.legacy.sak.config;

import no.nav.modiapersonoversikt.legacy.sak.service.InnsynJournalV2ServiceImpl;
import no.nav.modiapersonoversikt.legacy.sak.service.JournalV2ServiceImpl;
import no.nav.modiapersonoversikt.legacy.sak.service.SaksoversiktServiceImpl;
import no.nav.modiapersonoversikt.legacy.sak.service.TilgangskontrollServiceImpl;
import no.nav.modiapersonoversikt.legacy.sak.service.interfaces.InnsynJournalV2Service;
import no.nav.modiapersonoversikt.legacy.sak.service.interfaces.JournalV2Service;
import no.nav.modiapersonoversikt.legacy.sak.service.interfaces.SaksoversiktService;
import no.nav.modiapersonoversikt.legacy.sak.service.interfaces.TilgangskontrollService;
import no.nav.tjeneste.virksomhet.innsynjournal.v2.binding.InnsynJournalV2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ServiceConfig.class})
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
    public JournalV2Service journalV2Service() {
        return new JournalV2ServiceImpl();
    }

    @Bean
    public InnsynJournalV2Service innsynJournalV2Service(InnsynJournalV2 innsynJournalV2){
        return new InnsynJournalV2ServiceImpl(innsynJournalV2);
    }
}

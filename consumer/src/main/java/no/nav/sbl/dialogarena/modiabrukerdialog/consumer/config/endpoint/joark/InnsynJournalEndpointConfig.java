package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.joark;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.innsynjournal.v2.binding.InnsynJournalV2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class InnsynJournalEndpointConfig {

    public static final String INNSYN_JOURNAL_V2_URL = "INNSYNJOURNAL_V2_ENDPOINTURL";

    @Bean
    public InnsynJournalV2 innsynJournalV2() throws Exception {
        InnsynJournalV2 prod = createInnsynJournalV2PortType().configureStsForSubject().build();
        return createTimerProxyForWebService("InnsynJournalV2Service", prod, InnsynJournalV2.class);
    }

    @Bean
    public Pingable innsynJornalV2Ping() {
        return new PingableWebService("Joark - InnsynJournal_v2",
                createInnsynJournalV2PortType()
                        .configureStsForSystemUser()
                        .build());
    }

    private CXFClient<InnsynJournalV2> createInnsynJournalV2PortType() {
        return new CXFClient<>(InnsynJournalV2.class)
                .timeout(30000, 30000)
                .address(System.getProperty(INNSYN_JOURNAL_V2_URL))
                .enableMtom();
    }
}

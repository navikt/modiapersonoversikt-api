package no.nav.modiapersonoversikt.config.endpoint.joark;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.modiapersonoversikt.infrastructure.ping.PingableWebService;
import no.nav.modiapersonoversikt.infrastructure.types.Pingable;
import no.nav.tjeneste.virksomhet.innsynjournal.v2.binding.InnsynJournalV2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Autowired;

import static no.nav.modiapersonoversikt.infrastructure.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class InnsynJournalEndpointConfig {

    @Autowired
    private StsConfig stsConfig;
    public static final String INNSYN_JOURNAL_V2_URL = "INNSYNJOURNAL_V2_ENDPOINTURL";

    @Bean
    public InnsynJournalV2 innsynJournalV2() {
        InnsynJournalV2 prod = createInnsynJournalV2PortType().configureStsForSubject(stsConfig).build();
        return createTimerProxyForWebService("InnsynJournalV2Service", prod, InnsynJournalV2.class);
    }

    @Bean
    public Pingable innsynJornalV2Ping() {
        return new PingableWebService("Joark - InnsynJournal_v2",
                createInnsynJournalV2PortType()
                        .configureStsForSystemUser(stsConfig)
                        .build());
    }

    private CXFClient<InnsynJournalV2> createInnsynJournalV2PortType() {
        return new CXFClient<>(InnsynJournalV2.class)
                .timeout(30000, 30000)
                .address(EnvironmentUtils.getRequiredProperty(INNSYN_JOURNAL_V2_URL))
                .enableMtom();
    }
}

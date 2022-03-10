package no.nav.modiapersonoversikt.consumer.joark;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.modiapersonoversikt.infrastructure.ping.PingableWebService;
import no.nav.modiapersonoversikt.infrastructure.types.Pingable;
import no.nav.tjeneste.virksomhet.journal.v2.JournalV2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Autowired;

import static no.nav.modiapersonoversikt.infrastructure.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class JoarkEndpointConfig {
    @Autowired
    private StsConfig stsConfig;

    @Bean
    public JournalV2 journalV2() {
        JournalV2 prod = createJournalV2PortType().configureStsForSubject(stsConfig).build();
        return createTimerProxyForWebService("Joark - JournalV2Service", prod, JournalV2.class);
    }

    @Bean
    public Pingable pingJournalV2() {
        JournalV2 ws = createJournalV2PortType().configureStsForSystemUser(stsConfig).build();
        return new PingableWebService("Joark", ws);
    }


    private static CXFClient<JournalV2> createJournalV2PortType() {
        return new CXFClient<>(JournalV2.class)
                .timeout(30000, 30000)
                .address(EnvironmentUtils.getRequiredProperty("JOURNAL_V2_ENDPOINTURL"))
                .enableMtom();
    }
}

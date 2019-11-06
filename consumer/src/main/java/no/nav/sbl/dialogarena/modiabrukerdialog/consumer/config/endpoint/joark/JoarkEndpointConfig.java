package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.joark;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.journal.v2.JournalV2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class JoarkEndpointConfig {

    @Bean
    public JournalV2 journalV2() {
        JournalV2 prod = createJournalV2PortType().configureStsForSubject().build();
        return createTimerProxyForWebService("Joark - JournalV2Service", prod, JournalV2.class);
    }

    @Bean
    public Pingable pingJournalV2() {
        JournalV2 ws = createJournalV2PortType().configureStsForSystemUser().build();
        return new PingableWebService("Joark", ws);
    }


    private static CXFClient<JournalV2> createJournalV2PortType() {
        return new CXFClient<>(JournalV2.class)
                .timeout(30000, 30000)
                .address(System.getProperty("JOURNAL_V2_ENDPOINTURL"))
                .enableMtom();
    }
}

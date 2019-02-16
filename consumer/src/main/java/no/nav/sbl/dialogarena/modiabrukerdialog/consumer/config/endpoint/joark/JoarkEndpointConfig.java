package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.joark;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.util.EnvironmentUtils;
import no.nav.tjeneste.virksomhet.journal.v2.JournalV2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.JoarkPortTypeMock.createJournalV2Mock;

@Configuration
public class JoarkEndpointConfig {

    public static final String JOARK_KEY = "start.joark.withmock";

    @Bean
    public JournalV2 journalV2() {
        JournalV2 prod = createJournalV2PortType().configureStsForOnBehalfOfWithJWT().build();
        JournalV2 mock = createJournalV2Mock();
        return createMetricsProxyWithInstanceSwitcher("Joark - JournalV2Service", prod, mock, JOARK_KEY, JournalV2.class);
    }

    @Bean
    public Pingable pingJournalV2() {
        JournalV2 ws = createJournalV2PortType().configureStsForSystemUserInFSS().build();
        return new PingableWebService("Joark", ws);
    }


    private static CXFClient<JournalV2> createJournalV2PortType() {
        return new CXFClient<>(JournalV2.class)
                .timeout(30000, 30000)
                .address(System.getProperty("journal.v2.url"))
                .enableMtom();
    }
}

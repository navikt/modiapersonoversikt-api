package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.joark;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.journal.v2.JournalV2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.JoarkPortTypeMock.createInnsynJournalV1Mock;

@Configuration
public class JoarkEndpointConfig {

    public static final String JOARK_KEY = "start.joark.withmock";

    @Bean
    public JournalV2 joarkV2() {
        JournalV2 prod = createJournalV2PortType().configureStsForOnBehalfOfWithJWT().build();
        JournalV2 mock = createInnsynJournalV1Mock();
        return createMetricsProxyWithInstanceSwitcher("JournalV2", prod, mock, JOARK_KEY, JournalV2.class);
    }

    @Bean
    public Pingable pingJoark() {
        JournalV2 ws = createJournalV2PortType().configureStsForSystemUserInFSS().build();
        return new PingableWebService("Joark", ws);
    }


    private static CXFClient<JournalV2> createJournalV2PortType() {
        return new CXFClient<>(JournalV2.class)
                .timeout(30000, 30000)
                .address(getProperty("journal.v2.url"))
                .enableMtom();
    }
}

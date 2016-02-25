package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.joark;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.journal.v2.JournalV2;
import no.nav.tjeneste.virksomhet.journal.v2.JournalV2;
import no.nav.tjeneste.virksomhet.journal.v2.JournalV2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.JoarkPortTypeMock.*;

@Configuration
public class JoarkEndpointConfig {

    public static final String JOARK_KEY = "start.joark.withmock";

    @Bean
    public JournalV2 joarkV2() {
        JournalV2 prod = createJournalV2PortType(new UserSAMLOutInterceptor());
        JournalV2 mock = createInnsynJournalV1Mock();
        return createSwitcher(prod, mock, JOARK_KEY, JournalV2.class);
    }

    @Bean
    public Pingable pingJoark() {
        JournalV2 ws = createJournalV2PortType(new SystemSAMLOutInterceptor());
        return new PingableWebService("Joark", ws);
    }


    private static JournalV2 createJournalV2PortType(AbstractSAMLOutInterceptor interceptor) {
        return new CXFClient<>(JournalV2.class)
                .address(getProperty("journal.v2.url"))
                .withOutInterceptor(interceptor)
                .build();
    }
}

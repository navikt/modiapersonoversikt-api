package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.joark;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.JoarkPortTypeMock;
import no.nav.tjeneste.virksomhet.innsynjournal.v1.InnsynJournalV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.JoarkPortTypeMock.*;

@Configuration
public class JoarkEndpointConfig {

    public static final String JOARK_KEY = "start.joark.withmock";

    @Bean
    public InnsynJournalV1 innsynJournalV1() {
        InnsynJournalV1 prod = createInnsynJournalV1(new UserSAMLOutInterceptor());
        InnsynJournalV1 mock = createInnsynJournalV1Mock();
        return createSwitcher(prod, mock, JOARK_KEY, InnsynJournalV1.class);
    }

    @Bean
    public Pingable pingJoark() {
        InnsynJournalV1 ws = createInnsynJournalV1(new SystemSAMLOutInterceptor());
        return new PingableWebService("Joark", ws);
    }


    private static InnsynJournalV1 createInnsynJournalV1(AbstractSAMLOutInterceptor interceptor) {
        return new CXFClient<>(InnsynJournalV1.class)
                .address(getProperty("innsyn.journal.v1.url"))
                .withOutInterceptor(interceptor)
                .build();
    }
}

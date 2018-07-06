package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.joark;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.innsynjournal.v2.binding.InnsynJournalV2;
import org.springframework.context.annotation.Bean;

import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.InnsynJournalV2PortTypeMock.createInnsynJournalV2PortTypeMock;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

public class InnsynJournalEndpointConfig {

    public static final String JOARK_KEY = "start.joark.withmock";

    @Bean
    public InnsynJournalV2 innsynJournalV2() {
        InnsynJournalV2 prod = createInnsynJournalV2PortType().configureStsForOnBehalfOfWithJWT().build();
        InnsynJournalV2 mock = createInnsynJournalV2PortTypeMock();
        return createMetricsProxyWithInstanceSwitcher("InnsynJournalV2Service", prod, mock, JOARK_KEY, InnsynJournalV2.class);
    }

    @Bean
    public Pingable innsynJornalV2Ping(){
        return new PingableWebService("Joark - InnsynJournal_v2",
                createInnsynJournalV2PortType()
                        .configureStsForSystemUserInFSS()
                        .build());
    }

    private CXFClient<InnsynJournalV2> createInnsynJournalV2PortType() {
        return new CXFClient<>(InnsynJournalV2.class)
                .timeout(30000, 30000)
                .address(getRequiredProperty("innsyn.journal.v2.url"))
                .enableMtom();
    }
}

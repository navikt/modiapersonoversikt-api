package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.joark;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.innsynjournal.v2.binding.InnsynJournalV2;
import no.nav.tjeneste.virksomhet.innsynjournal.v2.meldinger.IdentifiserJournalpostResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class InnsynJournalEndpointConfig {

    public static final String START_JOARK_WITHMOCK = "start.joark.withmock";
    public static final String INNSYN_JOURNAL_V2_URL = "innsyn.journal.v2.url";
    public static final String JOURNALPOST_ID_MOCK = "1234567";

    @Bean
    public InnsynJournalV2 innsynJournalV2() throws Exception {
        InnsynJournalV2 prod = createInnsynJournalV2PortType().configureStsForOnBehalfOfWithJWT().build();
        InnsynJournalV2 mock = createMock();
        return createMetricsProxyWithInstanceSwitcher("InnsynJournalV2Service", prod, mock, START_JOARK_WITHMOCK, InnsynJournalV2.class);
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
                .address(System.getProperty(INNSYN_JOURNAL_V2_URL))
                .enableMtom();
    }

    private InnsynJournalV2 createMock() throws Exception {
        InnsynJournalV2 mock = mock(InnsynJournalV2.class);

        when(mock.identifiserJournalpost(any()))
                .thenReturn(new IdentifiserJournalpostResponse()
                        .withJournalpostId(JOURNALPOST_ID_MOCK));

        return mock;
    }
}

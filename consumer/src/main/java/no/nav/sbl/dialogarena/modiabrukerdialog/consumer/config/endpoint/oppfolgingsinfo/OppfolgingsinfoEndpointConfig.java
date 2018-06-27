package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.oppfolgingsinfo;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.OppfolgingsinfoV1;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.meldinger.OppfolgingsstatusRequest;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.meldinger.OppfolgingsstatusResponse;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.meldinger.WSOppfolgingsdata;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class OppfolgingsinfoEndpointConfig {

    public static final String MOCK_KEY = "start.oppfolgingsinfo.v1.withmock";
    private static final String ENDPOINT_URL = "oppfolgingsinfo.v1.url";

    @Bean
    public OppfolgingsinfoV1 oppfolgingsinfo() {
        return createMetricsProxyWithInstanceSwitcher(
                "oppfolgingsinfoV1",
                lagEndpoint().configureStsForOnBehalfOfWithJWT().build(),
                lagMockEndpoint(),
                MOCK_KEY,
                OppfolgingsinfoV1.class
        );
    }

    private CXFClient<OppfolgingsinfoV1> lagEndpoint() {
        return new CXFClient<>(OppfolgingsinfoV1.class)
                .address(System.getProperty(ENDPOINT_URL));
    }

    @Bean
    public Pingable oppfolgingsinfoPing() {
        return new PingableWebService(
                "Veilarboppfolging - Oppfolgingsinfov1",
                lagEndpoint().configureStsForSystemUserInFSS().build()
        );
    }

    private OppfolgingsinfoV1 lagMockEndpoint() {
        OppfolgingsinfoV1 mock = mock(OppfolgingsinfoV1.class);

        when(mock.hentOppfolgingsstatus(any(OppfolgingsstatusRequest.class)))
                .thenReturn(new OppfolgingsstatusResponse()
                        .withWsOppfolgingsdata(new WSOppfolgingsdata()
                                .withErUnderOppfolging(true)
                                .withVeilederIdent("***REMOVED***")));

        return mock;
    }

}

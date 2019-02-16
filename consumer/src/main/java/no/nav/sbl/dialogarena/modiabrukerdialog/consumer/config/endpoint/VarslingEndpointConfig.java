package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.varsel.config.HentBrukerVarselMock;
import no.nav.sbl.util.EnvironmentUtils;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.BrukervarselV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;

@Configuration
public class VarslingEndpointConfig {

    public static final String VARSLING_KEY = "start.varsling.withmock";

    @Bean
    public BrukervarselV1 varslerPortType() {
        final BrukervarselV1 prod = createVarslingPortType().configureStsForOnBehalfOfWithJWT().build();
        final BrukervarselV1 mock = new HentBrukerVarselMock();

        return createMetricsProxyWithInstanceSwitcher("Varsler", prod, mock, VARSLING_KEY, BrukervarselV1.class);
    }

    @Bean
    public Pingable varslerPing() {
        final BrukervarselV1 ws = createVarslingPortType().configureStsForSystemUserInFSS().build();
        return new PingableWebService("Varsler", ws);
    }

    private static CXFClient<BrukervarselV1> createVarslingPortType() {
        return new CXFClient<>(BrukervarselV1.class)
                .address(System.getProperty("varsler.ws.url"));
    }
}

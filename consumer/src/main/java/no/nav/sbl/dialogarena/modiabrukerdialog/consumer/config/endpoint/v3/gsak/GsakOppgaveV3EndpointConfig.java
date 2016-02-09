package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v3.gsak;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.TimingMetricsProxy.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GsakOppgaveV3PortTypeMock.createOppgavePortTypeMock;

@Configuration
public class GsakOppgaveV3EndpointConfig {

    public static final String GSAK_V3_KEY = "start.gsak.oppgave.withmock";

    @Bean
    public OppgaveV3 gsakOppgavePortType() {
        OppgaveV3 prod = createOppgavePortType(new UserSAMLOutInterceptor());
        OppgaveV3 mock = createOppgavePortTypeMock();

        return createMetricsProxyWithInstanceSwitcher(prod, mock, GSAK_V3_KEY, OppgaveV3.class);
    }

    @Bean
    public Pingable gsakPing() {
        final OppgaveV3 ws = createOppgavePortType(new SystemSAMLOutInterceptor());
        return new PingableWebService("GSAK_V3", ws);
    }

    private static OppgaveV3 createOppgavePortType(AbstractSAMLOutInterceptor interceptor) {
        return new CXFClient<>(OppgaveV3.class)
                .address(System.getProperty("gsak.oppgave.v3.url"))
                .withOutInterceptor(interceptor)
                .build();
    }

}

package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v3.gsak;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.util.EnvironmentUtils;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class GsakOppgaveV3EndpointConfig {

    @Bean
    public OppgaveV3 gsakOppgavePortType() {
        OppgaveV3 prod = createOppgavePortType().configureStsForSubject().build();

        return createTimerProxyForWebService("OppgaveV3", prod, OppgaveV3.class);
    }

    @Bean
    public Pingable gsakOppgavePing() {
        final OppgaveV3 ws = createOppgavePortType().configureStsForSystemUser().build();
        return new PingableWebService("Gsak - oppgave", ws);
    }

    private static CXFClient<OppgaveV3> createOppgavePortType() {
        return new CXFClient<>(OppgaveV3.class)
                .address(EnvironmentUtils.getRequiredProperty("VIRKSOMHET_OPPGAVE_V3_ENDPOINTURL"));
    }

}

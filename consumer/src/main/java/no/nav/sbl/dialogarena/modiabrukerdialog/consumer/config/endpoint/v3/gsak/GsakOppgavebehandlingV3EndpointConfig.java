package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v3.gsak;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.util.EnvironmentUtils;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class GsakOppgavebehandlingV3EndpointConfig {

    @Bean
    public OppgavebehandlingV3 gsakOppgavebehandlingPortType() {
        OppgavebehandlingV3 prod = createOppgavebehandlingPortType().configureStsForSubject().build();

        return createTimerProxyForWebService("OppgavebehandlingV3", prod, OppgavebehandlingV3.class);
    }

    @Bean
    public Pingable gsakOppgavebehandlingPing() {
        final OppgavebehandlingV3 ws = createOppgavebehandlingPortType().configureStsForSystemUser().build();
        return new PingableWebService("Gsak - oppgavebehandling", ws);
    }

    private static CXFClient<OppgavebehandlingV3> createOppgavebehandlingPortType() {
        return new CXFClient<>(OppgavebehandlingV3.class)
                .address(EnvironmentUtils.getRequiredProperty("VIRKSOMHET_OPPGAVEBEHANDLING_V3_ENDPOINTURL"));
    }

}

package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v3.gsak;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Autowired;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class GsakOppgavebehandlingV3EndpointConfig {
    @Autowired
    private StsConfig stsConfig;

    @Bean
    public OppgavebehandlingV3 gsakOppgavebehandlingPortType() {
        OppgavebehandlingV3 prod = createOppgavebehandlingPortType().configureStsForSubject(stsConfig).build();

        return createTimerProxyForWebService("OppgavebehandlingV3", prod, OppgavebehandlingV3.class);
    }

    @Bean
    public Pingable gsakOppgavebehandlingPing() {
        final OppgavebehandlingV3 ws = createOppgavebehandlingPortType().configureStsForSystemUser(stsConfig).build();
        return new PingableWebService("Gsak - oppgavebehandling", ws);
    }

    private static CXFClient<OppgavebehandlingV3> createOppgavebehandlingPortType() {
        return new CXFClient<>(OppgavebehandlingV3.class)
                .address(EnvironmentUtils.getRequiredProperty("VIRKSOMHET_OPPGAVEBEHANDLING_V3_ENDPOINTURL"));
    }

}

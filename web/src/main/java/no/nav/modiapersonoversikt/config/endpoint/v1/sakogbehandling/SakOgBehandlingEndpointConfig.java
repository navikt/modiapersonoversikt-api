package no.nav.modiapersonoversikt.config.endpoint.v1.sakogbehandling;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.modiapersonoversikt.infrastructure.ping.PingableWebService;
import no.nav.modiapersonoversikt.infrastructure.types.Pingable;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.binding.SakOgBehandlingV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import javax.xml.namespace.QName;

import static no.nav.modiapersonoversikt.infrastructure.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class SakOgBehandlingEndpointConfig {
    @Autowired
    private StsConfig stsConfig;

    @Bean
    public SakOgBehandlingV1 sakOgBehandlingPortType() {
        final SakOgBehandlingV1 prod = createSakogbehandlingPortType().configureStsForSubject(stsConfig).build();

        return createTimerProxyForWebService("SakOgBehandling", prod, SakOgBehandlingV1.class);
    }

    @Bean
    public Pingable pingSakOgBehandling() {
        final SakOgBehandlingV1 ws = createSakogbehandlingPortType().configureStsForSystemUser(stsConfig).build();
        return new PingableWebService("Sak og behandling", ws);
    }

    private CXFClient<SakOgBehandlingV1> createSakogbehandlingPortType() {
        return new CXFClient<>(SakOgBehandlingV1.class)
                .timeout(15000, 15000)
                .wsdl("classpath:wsdl/no/nav/tjeneste/virksomhet/sakOgBehandling/v1/Binding.wsdl")
                .serviceName(new QName("http://nav.no/tjeneste/virksomhet/sakOgBehandling/v1/Binding", "SakOgBehandling_v1"))
                .endpointName(new QName("http://nav.no/tjeneste/virksomhet/sakOgBehandling/v1/Binding", "SakOgBehandling_v1Port"))
                .address(EnvironmentUtils.getRequiredProperty("SAKOGBEHANDLING_ENDPOINTURL"));
    }

}

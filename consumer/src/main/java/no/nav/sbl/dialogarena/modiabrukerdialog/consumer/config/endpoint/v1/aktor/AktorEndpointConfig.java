package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.aktor;

import no.nav.modig.jaxws.handlers.MDCOutHandler;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;


@Configuration
public class AktorEndpointConfig {

    @Value("${aktorid.ws.url}")
    private String aktoerUrl;

    private AktoerPortType aktoerPort() {
        return new CXFClient<>(AktoerPortType.class)
                .timeout(15000, 15000)
                .address(aktoerUrl)
                .wsdl("classpath:Aktoer.wsdl")
                .configureStsForSystemUser()
                .withHandler(new MDCOutHandler())
                .build();
    }

    @Bean
    public AktoerPortType aktoerPortType() {
        final AktoerPortType prod = aktoerPort();

        return createTimerProxyForWebService("Aktoer", prod, AktoerPortType.class);
    }


    @Bean
    public Pingable pingAktoer() {
        return new PingableWebService("Aktor", aktoerPort());
    }
}

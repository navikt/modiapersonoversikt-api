package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.aktor;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.modig.jaxws.handlers.MDCOutHandler;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;


@Configuration
public class AktorEndpointConfig {

    @Value("${AKTOER_V1_ENDPOINTURL}")
    private String aktoerUrl;

    @Inject
    private StsConfig stsConfig;

    private AktoerPortType aktoerPort() {
        return new CXFClient<>(AktoerPortType.class)
                .timeout(15000, 15000)
                .address(aktoerUrl)
                .wsdl("classpath:Aktoer.wsdl")
                .configureStsForSystemUser(stsConfig)
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

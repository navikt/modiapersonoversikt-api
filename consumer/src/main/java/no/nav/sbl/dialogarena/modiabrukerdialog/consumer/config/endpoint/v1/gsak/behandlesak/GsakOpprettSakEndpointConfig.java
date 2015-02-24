package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.gsak.behandlesak;

import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.behandlesak.v1.BehandleSakV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GsakOpprettSakEndpointConfig {
    @Bean
    public BehandleSakV1 behandleSakV1() {
        return new CXFClient<>(BehandleSakV1.class)
                .address(System.getProperty("gsak.behandlesak.v1"))
                .wsdl("classpath:no/nav/tjeneste/virksomhet/behandleSak/v1/behandleSak.wsdl")
                .withOutInterceptor(new SystemSAMLOutInterceptor())
                .build();
    }
}

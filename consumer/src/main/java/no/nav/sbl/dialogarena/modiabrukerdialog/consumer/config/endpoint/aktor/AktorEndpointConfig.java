package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.aktor;

import no.nav.modig.jaxws.handlers.MDCOutHandler;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.AktoerPortTypeMock;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.InstanceSwitcher.createSwitcher;


@Configuration
public class AktorEndpointConfig {

    public static final String AKTOER_KEY = "start.aktoer.withmock";

    @Value("${aktorid.ws.url}")
    private String aktoerUrl;

    private AktoerPortType aktoerPort() {
        return new CXFClient<>(AktoerPortType.class)
                .address(aktoerUrl)
                .wsdl("classpath:wsdl/no/nav/tjeneste/virksomhet/aktoer/v1/Aktoer.wsdl")
                .withOutInterceptor(new SystemSAMLOutInterceptor())
                .withHandler(new MDCOutHandler())
                .build();
    }

    @Bean
    public AktoerPortType aktoerPortType() {
        final AktoerPortType mock = new AktoerPortTypeMock().getAktoerPortTypeMock();
        final AktoerPortType prod = aktoerPort();
        return createSwitcher(prod, mock, AKTOER_KEY, AktoerPortType.class);
    }

}

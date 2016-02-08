package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.aktor;

import no.nav.modig.jaxws.handlers.MDCOutHandler;
import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.AktoerPortTypeMock;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.TimingMetricsProxy.createMetricsProxyWithInstanceSwitcher;


@Configuration
public class AktorEndpointConfig {

    public static final String AKTOER_KEY = "start.aktoer.withmock";

    @Value("${aktorid.ws.url}")
    private String aktoerUrl;

    private AktoerPortType aktoerPort() {
        return new CXFClient<>(AktoerPortType.class)
                .address(aktoerUrl)
                .wsdl("classpath:Aktoer.wsdl")
                .withOutInterceptor(new SystemSAMLOutInterceptor())
                .withHandler(new MDCOutHandler())
                .build();
    }

    @Bean
    public AktoerPortType aktoerPortType() {
        final AktoerPortType mock = new AktoerPortTypeMock().getAktoerPortTypeMock();
        final AktoerPortType prod = aktoerPort();

        return createMetricsProxyWithInstanceSwitcher(prod, mock, AKTOER_KEY, AktoerPortType.class);
    }


    @Bean
    public Pingable pingAktoer(final AktoerPortType ws) {
        return () -> {

            long start = currentTimeMillis();
            String name = "AKTOER";
            try {
                ws.ping();
                return asList(new PingResult(name, SERVICE_OK, currentTimeMillis() - start));
            } catch (Exception e) {
                return asList(new PingResult(name, SERVICE_FAIL, currentTimeMillis() - start));
            }
        };
    }
}

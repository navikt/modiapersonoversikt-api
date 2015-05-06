package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.sakogbehandling;

import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.SakOgBehandlingPortTypeMock;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandling_v1PortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.InstanceSwitcher.createSwitcher;

@Configuration
public class SakOgBehandlingEndpointConfig {

    public static final String SAKOGBEHANDLING_KEY = "start.sakogbehandling.withmock";

    @Bean
    public SakOgBehandling_v1PortType sakOgBehandlingPortType() {
        final SakOgBehandling_v1PortType prod = createSakogbehandlingPortType(new UserSAMLOutInterceptor());
        final SakOgBehandling_v1PortType mock = new SakOgBehandlingPortTypeMock().getSakOgBehandlingPortTypeMock();
        return createSwitcher(prod, mock, SAKOGBEHANDLING_KEY, SakOgBehandling_v1PortType.class);
    }

    @Bean
    public Pingable pingSakOgBehandling() {
        return new Pingable() {
            @Override
            public List<PingResult> ping() {

                long start = currentTimeMillis();
                String name = "SAKOGBEHANDLING";
                try {
                    createSakogbehandlingPortType(new SystemSAMLOutInterceptor()).ping();
                    return asList(new PingResult(name, SERVICE_OK, currentTimeMillis() - start));
                } catch (Exception e) {
                    return asList(new PingResult(name, SERVICE_FAIL, currentTimeMillis() - start));
                }
            }
        };
    }

    private SakOgBehandling_v1PortType createSakogbehandlingPortType(AbstractSAMLOutInterceptor interceptor) {
        return new CXFClient<>(SakOgBehandling_v1PortType.class)
                .wsdl("classpath:sakOgBehandling/no/nav/tjeneste/virksomhet/sakOgBehandling/v1/sakOgBehandling.wsdl")
                .address(System.getProperty("sakogbehandling.ws.url"))
                .withOutInterceptor(interceptor)
                .build();
    }

}

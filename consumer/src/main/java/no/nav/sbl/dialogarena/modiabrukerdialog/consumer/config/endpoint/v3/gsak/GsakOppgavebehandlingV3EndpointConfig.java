package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v3.gsak;

import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v3.gsak.GsakOppgaveV3EndpointConfig.GSAK_V3_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GsakOppgavebehandlingV3PortTypeMock.createOppgavebehandlingPortTypeMock;

@Configuration
public class GsakOppgavebehandlingV3EndpointConfig {

    @Bean
    public OppgavebehandlingV3 gsakOppgavebehandlingPortType() {
        return createSwitcher(createOppgavebehandlingPortType(new UserSAMLOutInterceptor()), createOppgavebehandlingPortTypeMock(), GSAK_V3_KEY, OppgavebehandlingV3.class);
    }

    @Bean
    public Pingable gsakPing() {
        final OppgavebehandlingV3 ws = createOppgavebehandlingPortType(new SystemSAMLOutInterceptor());
        return new Pingable() {
            @Override
            public List<PingResult> ping() {
                long start = System.currentTimeMillis();
                String name = "GSAK_V3";
                try {
                    ws.ping();
                    return asList(new PingResult(name, SERVICE_OK, System.currentTimeMillis() - start));
                } catch (Exception e) {
                    return asList(new PingResult(name, SERVICE_FAIL, System.currentTimeMillis() - start));
                }
            }
        };
    }

    private static OppgavebehandlingV3 createOppgavebehandlingPortType(AbstractSAMLOutInterceptor interceptor) {
        return new CXFClient<>(OppgavebehandlingV3.class)
                .address(System.getProperty("gsak.oppgavebehandling.v3.url"))
                .withOutInterceptor(interceptor)
                .build();
    }

}

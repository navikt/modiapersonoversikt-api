package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v3.gsak;

import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GsakOppgavebehandlingV3PortTypeMock.createOppgavebehandlingPortTypeMock;

@Configuration
public class GsakOppgavebehandlingV3EndpointConfig {

    public static final String GSAK_V3_KEY = "start.gsak.oppgave.withmock";

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
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setAddress(System.getProperty("gsak.oppgave.v3.url"));
        proxyFactoryBean.setServiceClass(OppgaveV3.class);
        proxyFactoryBean.getOutInterceptors().add(interceptor);
        List<Feature> features = proxyFactoryBean.getFeatures();
        features.add(new LoggingFeature());
        features.add(new WSAddressingFeature());

        return proxyFactoryBean.create(OppgavebehandlingV3.class);
    }

}

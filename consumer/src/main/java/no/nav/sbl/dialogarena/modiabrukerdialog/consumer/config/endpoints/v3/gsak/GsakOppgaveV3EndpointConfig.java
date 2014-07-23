package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v3.gsak;

import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
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
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GsakOppgaveV3PortTypeMock.createOppgavePortTypeMock;

@Configuration
public class GsakOppgaveV3EndpointConfig {

    public static final String GSAK_V3_KEY = "start.gsak.oppgave.withmock";

    @Bean
    public OppgaveV3 gsakOppgavePortType() {
        return createSwitcher(createOppgavePortType(new UserSAMLOutInterceptor()), createOppgavePortTypeMock(), GSAK_V3_KEY, OppgaveV3.class);
    }

    @Bean
    public Pingable gsakPing() {
        final OppgaveV3 ws = createOppgavePortType(new SystemSAMLOutInterceptor());
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

    private static OppgaveV3 createOppgavePortType(AbstractSAMLOutInterceptor interceptor) {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setAddress(System.getProperty("gsak.oppgave.v3.url"));
        proxyFactoryBean.setServiceClass(OppgaveV3.class);
        proxyFactoryBean.getOutInterceptors().add(interceptor);
        List<Feature> features = proxyFactoryBean.getFeatures();
        features.add(new LoggingFeature());
        features.add(new WSAddressingFeature());

        return proxyFactoryBean.create(OppgaveV3.class);
    }

}

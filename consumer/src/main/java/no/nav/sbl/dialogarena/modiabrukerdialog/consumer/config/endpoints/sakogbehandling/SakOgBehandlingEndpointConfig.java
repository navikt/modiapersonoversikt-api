package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.sakogbehandling;

import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.SakOgBehandlingPortTypeMock;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.HentBehandlingBehandlingIkkeFunnet;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.HentBehandlingskjedensBehandlingerBehandlingskjedeIkkeFunnet;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandling_v1PortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerResponse;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jws.WebParam;
import java.util.List;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.MockUtil.mockErTillattOgSlaattPaaForKey;

@Configuration
public class SakOgBehandlingEndpointConfig {

    public static final String SAKOGBEHANDLING_KEY = "start.sakogbehandling.withmock";

    @Bean
    public SakOgBehandling_v1PortType sakOgBehandlingPortType() {
        final SakOgBehandling_v1PortType prod = createSakogbehandlingPortType(new UserSAMLOutInterceptor());
        final SakOgBehandling_v1PortType mock = new SakOgBehandlingPortTypeMock().getSakOgBehandlingPortTypeMock();
        return new SakOgBehandling_v1PortType() {

            @Cacheable("endpointCache")
            @Override
            public FinnSakOgBehandlingskjedeListeResponse finnSakOgBehandlingskjedeListe(@WebParam(name = "request", targetNamespace = "") FinnSakOgBehandlingskjedeListeRequest request) {
                if (mockErTillattOgSlaattPaaForKey(SAKOGBEHANDLING_KEY)) {
                    return mock.finnSakOgBehandlingskjedeListe(request);
                }
                return prod.finnSakOgBehandlingskjedeListe(request);
            }

            @Override
            public void ping() {
                if (mockErTillattOgSlaattPaaForKey(SAKOGBEHANDLING_KEY)) {
                    mock.ping();
                } else {
                    prod.ping();
                }
            }

            @Override
            public HentBehandlingskjedensBehandlingerResponse hentBehandlingskjedensBehandlinger
                    (@WebParam(name = "request", targetNamespace = "") HentBehandlingskjedensBehandlingerRequest hentBehandlingskjedensBehandlingerRequest)
                    throws HentBehandlingskjedensBehandlingerBehandlingskjedeIkkeFunnet {
                throw new RuntimeException("skal ikke brukes");
            }

            @Override
            public HentBehandlingResponse hentBehandling(@WebParam(name = "request", targetNamespace = "") HentBehandlingRequest hentBehandlingRequest) throws HentBehandlingBehandlingIkkeFunnet {
                throw new RuntimeException("skal ikke brukes");
            }
        };
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
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setWsdlLocation("classpath:sakOgBehandling/no/nav/virksomhet/tjenester/sakOgBehandling/v1/sakOgBehandling.wsdl");
        proxyFactoryBean.setAddress(System.getProperty("sakogbehandling.ws.url"));
        proxyFactoryBean.setServiceClass(SakOgBehandling_v1PortType.class);
        proxyFactoryBean.getOutInterceptors().add(interceptor);
        proxyFactoryBean.getFeatures().add(new WSAddressingFeature());
        proxyFactoryBean.getFeatures().add(new LoggingFeature());
        return proxyFactoryBean.create(SakOgBehandling_v1PortType.class);
    }

}

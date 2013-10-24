package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.HentBehandlingHentBehandlingBehandlingIkkeFunnet;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.HentBehandlingskjedensBehandlingerHentBehandlingskjedensBehandlingerBehandlingskjedeIkkeFunnet;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerResponse;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jws.WebParam;
import java.net.URL;

@Configuration
public class SakOgBehandlingEndpointConfig {

    @Value("${sakogbehandling.url}")
    private URL sakogbehandlingEndpoint;

    @Bean
    public SakOgBehandlingPortType sakOgBehandlingPortType() {
        SakOgBehandlingPortType sakOgBehandlingPortType = createSakOgBehandlingPortType(new UserSAMLOutInterceptor());
        return sakOgBehandlingPortType;
    }

    @Bean
    public SakOgBehandlingPortType selfTestSakOgBehandlingPortType() {
        SakOgBehandlingPortType sakOgBehandlingPortType = createSakOgBehandlingPortType(new SystemSAMLOutInterceptor());
        return sakOgBehandlingPortType;
    }

    private SakOgBehandlingPortType createSakOgBehandlingPortType(AbstractSAMLOutInterceptor interceptor) {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setWsdlLocation("sakOgBehandling/no/nav/tjeneste/virksomhet/sakOgBehandling/v1/SakOgBehandling.wsdl");
        proxyFactoryBean.setAddress(sakogbehandlingEndpoint.toString());
        proxyFactoryBean.setServiceClass(SakOgBehandlingPortType.class);
        proxyFactoryBean.getOutInterceptors().add(interceptor);
        proxyFactoryBean.getFeatures().add(new WSAddressingFeature());
        proxyFactoryBean.getFeatures().add(new LoggingFeature());
        final SakOgBehandlingPortType sakOgBehandlingPortType = proxyFactoryBean.create(SakOgBehandlingPortType.class);
        return new SakOgBehandlingPortType() {
            @Override
            public FinnSakOgBehandlingskjedeListeResponse finnSakOgBehandlingskjedeListe(@WebParam(name = "request", targetNamespace = "") FinnSakOgBehandlingskjedeListeRequest finnSakOgBehandlingskjedeListeRequest) {
                return sakOgBehandlingPortType.finnSakOgBehandlingskjedeListe(finnSakOgBehandlingskjedeListeRequest);
            }

            @Override
            public HentBehandlingskjedensBehandlingerResponse hentBehandlingskjedensBehandlinger(@WebParam(name = "request", targetNamespace = "") HentBehandlingskjedensBehandlingerRequest hentBehandlingskjedensBehandlingerRequest) throws HentBehandlingskjedensBehandlingerHentBehandlingskjedensBehandlingerBehandlingskjedeIkkeFunnet {
                return sakOgBehandlingPortType.hentBehandlingskjedensBehandlinger(hentBehandlingskjedensBehandlingerRequest);
            }

            @Override
            public HentBehandlingResponse hentBehandling(@WebParam(name = "request", targetNamespace = "") HentBehandlingRequest hentBehandlingRequest) throws HentBehandlingHentBehandlingBehandlingIkkeFunnet {
                return sakOgBehandlingPortType.hentBehandling(hentBehandlingRequest);
            }

            @Override
            public void ping() {
                sakOgBehandlingPortType.ping();
            }
        };
    }

}

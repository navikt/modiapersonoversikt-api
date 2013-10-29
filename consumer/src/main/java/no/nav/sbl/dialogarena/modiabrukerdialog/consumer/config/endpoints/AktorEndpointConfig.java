package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentResponse;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jws.WebParam;
import java.net.URL;
import java.util.List;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;

@Configuration
public class AktorEndpointConfig {

    @Value("${aktor.url}")
    private URL aktorEndpoint;

    @Bean
    public AktoerPortType aktorPortType() {
        return createAktorIdPortType(new UserSAMLOutInterceptor());
    }

    @Bean
    public Pingable aktorIdPing() {
        final AktoerPortType aktorIdPortType = createAktorIdPortType(new SystemSAMLOutInterceptor());
        return new Pingable() {
            @Override
            public List<PingResult> ping() {
                PingResult.ServiceResult result;
                long start = currentTimeMillis();
                try {
                    aktorIdPortType.ping();
                    result = SERVICE_OK;
                } catch (Exception e) {
                    result = SERVICE_FAIL;
                }
                return asList(new PingResult("Aktoer_v1", result, currentTimeMillis() - start));
            }
        };
    }

    private AktoerPortType createAktorIdPortType(AbstractSAMLOutInterceptor interceptor) {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setWsdlLocation("wsdl/no/nav/tjeneste/virksomhet/aktoer/v1/Aktoer.wsdl");
        proxyFactoryBean.setAddress(aktorEndpoint.toString());
        proxyFactoryBean.setServiceClass(AktoerPortType.class);
        proxyFactoryBean.getOutInterceptors().add(interceptor);
        proxyFactoryBean.getFeatures().add(new WSAddressingFeature());
        proxyFactoryBean.getFeatures().add(new LoggingFeature());
        final AktoerPortType aktoerPortType = proxyFactoryBean.create(AktoerPortType.class);
        return new AktoerPortType() {

            @Cacheable("endpointCache")
            @Override
            public HentAktoerIdForIdentResponse hentAktoerIdForIdent(@WebParam(name = "request", targetNamespace = "") HentAktoerIdForIdentRequest hentAktoerIdForIdentRequest) throws HentAktoerIdForIdentPersonIkkeFunnet {
                return aktoerPortType.hentAktoerIdForIdent(hentAktoerIdForIdentRequest);
            }

            @Cacheable("endpointCache")
            @Override
            public void ping() {
                aktoerPortType.ping();
            }
        };
    }
}

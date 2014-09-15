package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.henvendelse;

import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.virksomhet.tjenester.sak.arbeidogaktivitet.v1.ArbeidOgAktivitet;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.ArbeidOgAktivitetMock.createArbeidOgAktivitetMock;

@Configuration
public class ArbeidOgAktivitetEndpointConfig {

    public static final String ARENA_KEY = "start.arena.arbeidOgAktivitet.withmock";

    @Bean
    public ArbeidOgAktivitet arbeidOgAktivitet() {
        return createSwitcher(
                createArbeidOgAktivitetPortType(new UserSAMLOutInterceptor()),
                createArbeidOgAktivitetMock(),
                ARENA_KEY,
                ArbeidOgAktivitet.class
        );
    }

    private static ArbeidOgAktivitet createArbeidOgAktivitetPortType(AbstractSAMLOutInterceptor interceptor) {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setAddress(System.getProperty("arbeidOgAktivitet.v2.url"));
        proxyFactoryBean.setServiceClass(ArbeidOgAktivitet.class);
        proxyFactoryBean.getOutInterceptors().add(interceptor);
        proxyFactoryBean.getFeatures().add(new WSAddressingFeature());
        proxyFactoryBean.getFeatures().add(new LoggingFeature());
        proxyFactoryBean.setProperties(new HashMap<String, Object>());
        ArbeidOgAktivitet portType = proxyFactoryBean.create(ArbeidOgAktivitet.class);
        Client client = ClientProxy.getClient(portType);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        TLSClientParameters clientParameters = new TLSClientParameters();
        clientParameters.setDisableCNCheck(true);
        httpConduit.setTlsClientParameters(clientParameters);
        return portType;
    }

}

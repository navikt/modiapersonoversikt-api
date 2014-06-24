package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v2.gsak;

import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.Oppgavebehandling;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v2.gsak.GsakOppgaveV2EndpointConfig.GSAK_V2_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GsakOppgavebehandlingV2PortTypeMock.createOppgavebehandlingPortTypeMock;

@Configuration
public class GsakOppgavebehandlingV2EndpointConfig {

    @Bean
    public Oppgavebehandling gsakOppgavebehandlingPortType() {
        return createSwitcher(createOppgavebehandlingPortType(), createOppgavebehandlingPortTypeMock(), GSAK_V2_KEY, Oppgavebehandling.class);
    }

    private static Oppgavebehandling createOppgavebehandlingPortType() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setWsdlLocation("classpath:oppgavebehandling/no/nav/virksomhet/tjenester/oppgavebehandling/oppgavebehandling.wsdl");
        proxyFactoryBean.setAddress(System.getProperty("gsak.oppgavebehandling.v2.url"));
        proxyFactoryBean.getOutInterceptors().add(new UserSAMLOutInterceptor());
        proxyFactoryBean.getFeatures().add(new WSAddressingFeature());
        proxyFactoryBean.getFeatures().add(new LoggingFeature());
        Oppgavebehandling portType = proxyFactoryBean.create(Oppgavebehandling.class);
        Client client = ClientProxy.getClient(portType);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        TLSClientParameters clientParameters = new TLSClientParameters();
        clientParameters.setDisableCNCheck(true);
        httpConduit.setTlsClientParameters(clientParameters);
        return portType;
    }
}

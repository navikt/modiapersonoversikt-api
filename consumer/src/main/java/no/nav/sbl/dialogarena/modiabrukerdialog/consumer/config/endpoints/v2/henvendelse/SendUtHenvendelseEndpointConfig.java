package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v2.henvendelse;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLBehandlingsinformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLReferat;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLSporsmal;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLSvar;
import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
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
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v2.henvendelse.HenvendelseEndpointConfig.HENVENDELSE_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.SendUtHenvendelsePortTypeMock.createSendUtHenvendelsePortTypeMock;

@Configuration
public class SendUtHenvendelseEndpointConfig {

    @Bean
    public SendUtHenvendelsePortType sendUtHenvendelsePortType() {
        return createSwitcher(
                createSendUtHenvendelsePortType(new UserSAMLOutInterceptor()),
                createSendUtHenvendelsePortTypeMock(),
                HENVENDELSE_KEY,
                SendUtHenvendelsePortType.class
        );
    }

    @Bean
    public Pingable sendUtHenvendelsePing() {
        final SendUtHenvendelsePortType ws = createSendUtHenvendelsePortType(new SystemSAMLOutInterceptor());
        return new Pingable() {
            @Override
            public List<PingResult> ping() {
                long start = System.currentTimeMillis();
                String name = "SEND_UT_HENVENDELSE";
                try {
                    ws.ping();
                    return asList(new PingResult(name, SERVICE_OK, System.currentTimeMillis() - start));
                } catch (Exception e) {
                    return asList(new PingResult(name, SERVICE_FAIL, System.currentTimeMillis() - start));
                }
            }
        };
    }

    private static SendUtHenvendelsePortType createSendUtHenvendelsePortType(AbstractSAMLOutInterceptor interceptor) {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setWsdlLocation("classpath:SendUtHenvendelse.wsdl");
        proxyFactoryBean.setAddress(System.getProperty("send.ut.henvendelse.url"));
        proxyFactoryBean.setServiceClass(SendUtHenvendelsePortType.class);
        proxyFactoryBean.getOutInterceptors().add(interceptor);
        proxyFactoryBean.getFeatures().add(new WSAddressingFeature());
        proxyFactoryBean.getFeatures().add(new LoggingFeature());
        proxyFactoryBean.setProperties(new HashMap<String, Object>());
        proxyFactoryBean.getProperties().put("jaxb.additionalContextClasses", new Class[]{
                XMLBehandlingsinformasjon.class,
                XMLMetadataListe.class,
                XMLSporsmal.class,
                XMLSvar.class,
                XMLReferat.class});
        SendUtHenvendelsePortType portType = proxyFactoryBean.create(SendUtHenvendelsePortType.class);
        Client client = ClientProxy.getClient(portType);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        TLSClientParameters clientParameters = new TLSClientParameters();
        clientParameters.setDisableCNCheck(true);
        httpConduit.setTlsClientParameters(clientParameters);
        return portType;
    }

}

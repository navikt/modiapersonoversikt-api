package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v2.henvendelseinformasjon;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLBehandlingsinformasjonV2;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLMetadataListe;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLReferat;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLSporsmal;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLSvar;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.informasjon.v2.HenvendelseInformasjonV2PortType;
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

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelseInformasjonV2PortTypeMock.createHenvendelseInformasjonV2PortTypeMock;

@Configuration
public class HenvendelseInformasjonV2EndpointConfig {

    public static final String HENVENDELSE_INFORMASJON_V2_KEY = "start.henvendelseinformasjonv2.withmock";

    @Bean
    public HenvendelseInformasjonV2PortType henvendelseInformasjonV2PortType() {
        return createSwitcher(createHenvendelseInformasjonV2PortType(), createHenvendelseInformasjonV2PortTypeMock(), HENVENDELSE_INFORMASJON_V2_KEY, HenvendelseInformasjonV2PortType.class);
    }

    private static HenvendelseInformasjonV2PortType createHenvendelseInformasjonV2PortType() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setWsdlLocation("classpath:HenvendelseInformasjonV2.wsdl");
        proxyFactoryBean.setAddress("https://localhost:8443/henvendelse/services/domene.Brukerdialog/HenvendelseInformasjon_v2");
        proxyFactoryBean.setServiceClass(HenvendelseInformasjonV2PortType.class);
        proxyFactoryBean.getOutInterceptors().add(new UserSAMLOutInterceptor());
        proxyFactoryBean.getFeatures().add(new WSAddressingFeature());
        proxyFactoryBean.getFeatures().add(new LoggingFeature());
        proxyFactoryBean.setProperties(new HashMap<String, Object>());
        proxyFactoryBean.getProperties().put("jaxb.additionalContextClasses", new Class[]{
                XMLBehandlingsinformasjonV2.class,
                XMLMetadataListe.class,
                XMLSporsmal.class,
                XMLSvar.class,
                XMLReferat.class});
        HenvendelseInformasjonV2PortType portType = proxyFactoryBean.create(HenvendelseInformasjonV2PortType.class);
        Client client = ClientProxy.getClient(portType);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        TLSClientParameters clientParameters = new TLSClientParameters();
        clientParameters.setDisableCNCheck(true);
        httpConduit.setTlsClientParameters(clientParameters);
        return portType;
    }
}

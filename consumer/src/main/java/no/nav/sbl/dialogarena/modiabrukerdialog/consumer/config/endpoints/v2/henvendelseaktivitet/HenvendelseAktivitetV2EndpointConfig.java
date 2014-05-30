package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v2.henvendelseaktivitet;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLBehandlingsinformasjonV2;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLMetadataListe;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLReferat;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLSporsmal;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLSvar;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.aktivitet.v2.HenvendelseAktivitetV2PortType;
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
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelseAktivitetV2PortTypeMock.createHenvendelseAktivitetV2PortTypeMock;

@Configuration
public class HenvendelseAktivitetV2EndpointConfig {

    public static final String HENVENDELSE_AKTIVITET_V2_KEY = "start.henvendelseaktivitetv2.withmock";

    @Bean
    public HenvendelseAktivitetV2PortType henvendelseAktivitetV2PortType() {
        return createSwitcher(createHenvendelseAktivitetV2PortType(), createHenvendelseAktivitetV2PortTypeMock(), HENVENDELSE_AKTIVITET_V2_KEY, HenvendelseAktivitetV2PortType.class);
    }

    private static HenvendelseAktivitetV2PortType createHenvendelseAktivitetV2PortType() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setWsdlLocation("classpath:HenvendelseAktivitetV2.wsdl");
        proxyFactoryBean.setAddress("https://localhost:8443/henvendelse/services/domene.Brukerdialog/HenvendelseAktivitet_v2");
        proxyFactoryBean.setServiceClass(HenvendelseAktivitetV2PortType.class);
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
        HenvendelseAktivitetV2PortType portType = proxyFactoryBean.create(HenvendelseAktivitetV2PortType.class);
        Client client = ClientProxy.getClient(portType);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        TLSClientParameters clientParameters = new TLSClientParameters();
        clientParameters.setDisableCNCheck(true);
        httpConduit.setTlsClientParameters(clientParameters);
        return portType;
    }

}

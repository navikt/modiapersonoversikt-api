package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.porttypeimpl;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.common.integrasjon.features.TimeoutFeature;
import no.nav.sbl.dialogarena.common.kodeverk.CachingKodeverkClient;
import no.nav.sbl.dialogarena.common.kodeverk.DefaultKodeverkClient;
import no.nav.sbl.dialogarena.common.kodeverk.KodeverkClient;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.apache.cxf.ws.security.SecurityConstants;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.EndpointsConfig.MODIA_CONNECTION_TIMEOUT;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.EndpointsConfig.MODIA_RECEIVE_TIMEOUT;

public class KodeverkV2EndpointConfigImpl {

    private URL kodeverkEndpoint;

    public KodeverkV2EndpointConfigImpl(URL kodeverkEndpoint) {
        this.kodeverkEndpoint = kodeverkEndpoint;
    }

    public KodeverkPortType kodeverkPortType() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setServiceClass(KodeverkPortType.class);
        proxyFactoryBean.setAddress(kodeverkEndpoint.toString());
        proxyFactoryBean.setWsdlURL("classpath:kodeverk/no/nav/tjeneste/virksomhet/kodeverk/v2/Kodeverk.wsdl");

        //setter mustunderstand i header slik at tjenester som ikke forstår sikkerhetsheader ikke skal avvise requester
        proxyFactoryBean.setProperties(new HashMap<String, Object>());
        proxyFactoryBean.getProperties().put(SecurityConstants.MUST_UNDERSTAND, false);
        proxyFactoryBean.getFeatures().add(new WSAddressingFeature());
        proxyFactoryBean.getFeatures().add(new LoggingFeature());
        proxyFactoryBean.getFeatures().add(new TimeoutFeature().withConnectionTimeout(MODIA_CONNECTION_TIMEOUT).withReceiveTimeout(MODIA_RECEIVE_TIMEOUT));

        return proxyFactoryBean.create(KodeverkPortType.class);
    }

    public KodeverkClient kodeverkClient() {
        return new CachingKodeverkClient(new DefaultKodeverkClient(kodeverkPortType()), new Optional.None<File>());
    }
}

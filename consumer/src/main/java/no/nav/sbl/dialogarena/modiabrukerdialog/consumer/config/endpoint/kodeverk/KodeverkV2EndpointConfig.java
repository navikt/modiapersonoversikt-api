package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.kodeverk;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.common.integrasjon.features.TimeoutFeature;
import no.nav.sbl.dialogarena.common.kodeverk.CachingKodeverkClient;
import no.nav.sbl.dialogarena.common.kodeverk.DefaultKodeverkClient;
import no.nav.sbl.dialogarena.common.kodeverk.KodeverkClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.KodeverkV2PortTypeMock;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.EndpointsConfig.MODIA_CONNECTION_TIMEOUT;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.EndpointsConfig.MODIA_RECEIVE_TIMEOUT;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.InstanceSwitcher.createSwitcher;
import static org.apache.cxf.ws.security.SecurityConstants.MUST_UNDERSTAND;

@Configuration
public class KodeverkV2EndpointConfig {

    public static final String KODEVERK_KEY = "start.kodeverk.withmock";

    @Value("${kodeverkendpoint.v2.url}")
    private URL endpoint;

    @Bean(name = "kodeverkPortTypeV2")
    public KodeverkPortType kodeverkPortType() {
        KodeverkPortType prod = lagKodeverkPortType();
        KodeverkPortType mock = KodeverkV2PortTypeMock.kodeverkPortType();
        return createSwitcher(prod, mock, KODEVERK_KEY, KodeverkPortType.class);
    }

    @Bean
    public KodeverkClient kodeverkClient() {
        KodeverkClient prod = lagKodeverkClient();
        KodeverkClient mock = KodeverkV2PortTypeMock.kodeverkClient();
        return createSwitcher(prod, mock, KODEVERK_KEY, KodeverkClient.class);
    }

    private KodeverkPortType lagKodeverkPortType() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setServiceClass(KodeverkPortType.class);
        proxyFactoryBean.setAddress(endpoint != null ? endpoint.toString() : "Address not set");
        proxyFactoryBean.setWsdlURL("classpath:kodeverk/no/nav/tjeneste/virksomhet/kodeverk/v2/Kodeverk.wsdl");

        proxyFactoryBean.setProperties(new HashMap<String, Object>());

        //setter mustunderstand i header slik at tjenester som ikke forstår sikkerhetsheader ikke skal avvise requester
        proxyFactoryBean.getProperties().put(MUST_UNDERSTAND, false);
        proxyFactoryBean.getFeatures().add(new WSAddressingFeature());
        proxyFactoryBean.getFeatures().add(new LoggingFeature());
        proxyFactoryBean.getFeatures().add(new TimeoutFeature().withConnectionTimeout(MODIA_CONNECTION_TIMEOUT).withReceiveTimeout(MODIA_RECEIVE_TIMEOUT));

        return proxyFactoryBean.create(KodeverkPortType.class);
    }

    private KodeverkClient lagKodeverkClient() {
        return new CachingKodeverkClient(new DefaultKodeverkClient(kodeverkPortType()), new Optional.None<File>());
    }

}

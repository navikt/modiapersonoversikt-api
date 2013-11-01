package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.integrasjon.features.TimeoutFeature;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;

import java.net.URL;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.EndpointsConfig.MODIA_CONNECTION_TIMEOUT;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.EndpointsConfig.MODIA_RECEIVE_TIMEOUT;

public class SakOgBehandlingPortTypeImpl {

    public SakOgBehandlingPortType sakOgBehandlingPortType(URL sakogbehandlingEndpoint) {
        return createSakOgBehandlingPortType(new UserSAMLOutInterceptor(), sakogbehandlingEndpoint);
    }

    public SakOgBehandlingPortType selfTestSakOgBehandlingPortType(URL sakogbehandlingEndpoint) {
        return createSakOgBehandlingPortType(new SystemSAMLOutInterceptor(), sakogbehandlingEndpoint);
    }

    private SakOgBehandlingPortType createSakOgBehandlingPortType(AbstractSAMLOutInterceptor interceptor, URL sakogbehandlingEndpoint) {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setWsdlLocation("sakOgBehandling/no/nav/tjeneste/virksomhet/sakOgBehandling/v1/SakOgBehandling.wsdl");
        proxyFactoryBean.setAddress(sakogbehandlingEndpoint.toString());
        proxyFactoryBean.setServiceClass(SakOgBehandlingPortType.class);
        proxyFactoryBean.getOutInterceptors().add(interceptor);
        proxyFactoryBean.getFeatures().add(new WSAddressingFeature());
        proxyFactoryBean.getFeatures().add(new LoggingFeature());
        proxyFactoryBean.getFeatures().add(new TimeoutFeature().withConnectionTimeout(MODIA_CONNECTION_TIMEOUT).withReceiveTimeout(MODIA_RECEIVE_TIMEOUT));
        return proxyFactoryBean.create(SakOgBehandlingPortType.class);
    }
}

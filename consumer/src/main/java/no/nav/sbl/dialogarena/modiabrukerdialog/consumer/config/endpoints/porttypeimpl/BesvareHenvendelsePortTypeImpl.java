package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.porttypeimpl;

import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.integrasjon.features.TimeoutFeature;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;

import java.util.List;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.EndpointsConfig.MODIA_CONNECTION_TIMEOUT;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.EndpointsConfig.MODIA_RECEIVE_TIMEOUT;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.Utils.konfigurerMedHttps;

public class BesvareHenvendelsePortTypeImpl {

    protected String besvareHenvendelseEndpoint;

    public BesvareHenvendelsePortTypeImpl(String besvareHenvendelseEndpoint) {
        this.besvareHenvendelseEndpoint = besvareHenvendelseEndpoint;
    }

    public BesvareHenvendelsePortType besvareHenvendelsePortType() {
        return lagBesvareHenvendelsePortType(new UserSAMLOutInterceptor());
    }

    public Pingable besvareHenvendelsePing() {
        return new Pingable() {
            @Override
            public List<PingResult> ping() {
                BesvareHenvendelsePortType besvareHenvendelsePortType = lagBesvareHenvendelsePortType(new SystemSAMLOutInterceptor());
                long start = currentTimeMillis();
                boolean success;
                try {
                    success = besvareHenvendelsePortType.ping();
                } catch (Exception e) {
                    success = false;
                }
                return asList(new PingResult("BesvareHenvendelse_v1", success ? SERVICE_OK : SERVICE_FAIL, currentTimeMillis() - start));
            }
        };
    }

    private BesvareHenvendelsePortType lagBesvareHenvendelsePortType(AbstractSAMLOutInterceptor interceptor) {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        factoryBean.setWsdlURL("classpath:v1/BesvareHenvendelse.wsdl");
        factoryBean.getFeatures().add(new LoggingFeature());
        factoryBean.getFeatures().add(new WSAddressingFeature());
        factoryBean.getFeatures().add(new TimeoutFeature().withConnectionTimeout(MODIA_CONNECTION_TIMEOUT).withReceiveTimeout(MODIA_RECEIVE_TIMEOUT));
        factoryBean.getOutInterceptors().add(interceptor);
        factoryBean.setServiceClass(BesvareHenvendelsePortType.class);
        factoryBean.setAddress(besvareHenvendelseEndpoint);
        BesvareHenvendelsePortType besvareHenvendelsePortType = factoryBean.create(BesvareHenvendelsePortType.class);
        konfigurerMedHttps(besvareHenvendelsePortType);

        return besvareHenvendelsePortType;
    }

}

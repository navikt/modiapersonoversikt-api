package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.endpoints;

import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.config.endpoints.Utils.konfigurerMedHttps;

@Configuration
public class HenvendelseEndpointConfig {

    @Value("${henvendelseendpoint.url}")
    protected String henvendelseEndpoint;

    @Bean
    public HenvendelsePortType henvendelsePortType() {
        return lagHenvendelsePortType(new UserSAMLOutInterceptor());
    }

    @Bean
    public Pingable henvendelsePing() {
        return new Pingable() {
            @Override
            public List<PingResult> ping() {
                HenvendelsePortType henvendelsePortType = lagHenvendelsePortType(new SystemSAMLOutInterceptor());
                long start = System.currentTimeMillis();
                boolean success;
                try {
                    success = henvendelsePortType.ping();
                } catch (Exception e) {
                    success = false;
                }
                return asList(new PingResult("HenvendelseInnsyn_v1", success ? SERVICE_OK : SERVICE_FAIL, System.currentTimeMillis() - start));
            }
        };
    }

    private HenvendelsePortType lagHenvendelsePortType(AbstractSAMLOutInterceptor interceptor) {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        factoryBean.setWsdlURL("classpath:Henvendelse.wsdl");
        factoryBean.getFeatures().add(new LoggingFeature());
        factoryBean.getFeatures().add(new WSAddressingFeature());
        factoryBean.getOutInterceptors().add(interceptor);
        factoryBean.setServiceClass(HenvendelsePortType.class);
        factoryBean.setAddress(henvendelseEndpoint);
        HenvendelsePortType henvendelsePortType = factoryBean.create(HenvendelsePortType.class);
        konfigurerMedHttps(henvendelsePortType);

        return henvendelsePortType;
    }
}

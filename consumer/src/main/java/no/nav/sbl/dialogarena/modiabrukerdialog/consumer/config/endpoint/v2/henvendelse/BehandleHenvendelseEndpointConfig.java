package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.henvendelse;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLJournalfortInformasjon;
import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.henvendelse.HenvendelseEndpointConfig.HENVENDELSE_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.TLSOppsettUtils.skruAvSertifikatsjekkDersomLokalOppstart;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.BehandleHenvendelsePortTypeMock.createBehandleHenvendelsePortTypeMock;

@Configuration
public class BehandleHenvendelseEndpointConfig {

    @Bean
    public BehandleHenvendelsePortType behandleHenvendelsePortType() {
        return createSwitcher(
                createBehandleHenvendelsePortType(new UserSAMLOutInterceptor()),
                createBehandleHenvendelsePortTypeMock(),
                HENVENDELSE_KEY,
                BehandleHenvendelsePortType.class
        );
    }

    @Bean
    public Pingable behandleHenvendelsePing() {
        final BehandleHenvendelsePortType ws = createBehandleHenvendelsePortType(new SystemSAMLOutInterceptor());
        return new Pingable() {
            @Override
            public List<PingResult> ping() {
                long start = System.currentTimeMillis();
                String name = "BEHANDLE_HENVENDELSE";
                try {
                    ws.ping();
                    return asList(new PingResult(name, SERVICE_OK, System.currentTimeMillis() - start));
                } catch (Exception e) {
                    return asList(new PingResult(name, SERVICE_FAIL, System.currentTimeMillis() - start));
                }
            }
        };
    }

    private static BehandleHenvendelsePortType createBehandleHenvendelsePortType(AbstractSAMLOutInterceptor interceptor) {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setWsdlLocation("classpath:BehandleHenvendelse.wsdl");
        proxyFactoryBean.setAddress(System.getProperty("behandle.henvendelse.url"));
        proxyFactoryBean.setServiceClass(BehandleHenvendelsePortType.class);
        proxyFactoryBean.getOutInterceptors().add(interceptor);
        proxyFactoryBean.getFeatures().add(new WSAddressingFeature());
        proxyFactoryBean.getFeatures().add(new LoggingFeature());
        proxyFactoryBean.setProperties(new HashMap<String, Object>());
        proxyFactoryBean.getProperties().put("jaxb.additionalContextClasses", new Class[]{XMLJournalfortInformasjon.class});
        BehandleHenvendelsePortType portType = proxyFactoryBean.create(BehandleHenvendelsePortType.class);
        skruAvSertifikatsjekkDersomLokalOppstart(ClientProxy.getClient(portType));
        return portType;
    }

}

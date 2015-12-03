package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.utbetaling;

import no.nav.modig.modia.ping.PingableWebService;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.UtbetalingPortTypeMock;
import no.nav.tjeneste.virksomhet.utbetaling.v1.UtbetalingV1;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.QName;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.TimingMetricsProxy.createMetricsProxyWithInstanceSwitcher;

@Configuration
public class UtbetalingEndpointConfig {

    public static final String UTBETALING_KEY = "start.utbetaling.withmock";

    @Bean(name = "utbetalingV1")
    public UtbetalingV1 utbetalingV1() {
        final UtbetalingV1 prod = createUtbetalingPortType(new UserSAMLOutInterceptor());
        final UtbetalingV1 mock = new UtbetalingPortTypeMock().utbetalingPortType();

        return createMetricsProxyWithInstanceSwitcher(prod, mock, UTBETALING_KEY, UtbetalingV1.class);
    }

    @Bean
    public UtbetalingPing pingUtbetalingV1() {
        return new UtbetalingPing("Utbetaling", createUtbetalingPortType(new SystemSAMLOutInterceptor()));
    }

    private UtbetalingV1 createUtbetalingPortType(AbstractSAMLOutInterceptor interceptor) {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setWsdlLocation("classpath:utbetaling/no/nav/tjeneste/virksomhet/utbetaling/v1/Binding.wsdl");
        proxyFactoryBean.setAddress(System.getProperty("utbetalingendpoint.url"));
        proxyFactoryBean.setServiceClass(UtbetalingV1.class);
        proxyFactoryBean.getOutInterceptors().add(interceptor);
        proxyFactoryBean.setServiceName(new QName("http://nav.no/tjeneste/virksomhet/utbetaling/v1/Binding", "Utbetaling_v1"));
        proxyFactoryBean.setEndpointName(new QName("http://nav.no/tjeneste/virksomhet/utbetaling/v1/Binding", "Utbetaling_v1Port"));
        proxyFactoryBean.getFeatures().add(new WSAddressingFeature());
        proxyFactoryBean.getFeatures().add(new LoggingFeature());
        return proxyFactoryBean.create(UtbetalingV1.class);
    }

    public class UtbetalingPing extends PingableWebService {

        public UtbetalingPing(String name, Object webservice) {
            super(name, webservice);
        }
    }
}



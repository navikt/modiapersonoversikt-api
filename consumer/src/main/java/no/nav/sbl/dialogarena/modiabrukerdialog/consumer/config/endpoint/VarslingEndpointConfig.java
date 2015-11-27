package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint;

import no.nav.melding.domene.brukerdialog.varsler.v1.VarslerPorttype;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.TimingMetricsProxy;
import no.nav.sbl.dialogarena.varsel.config.VarslerMock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VarslingEndpointConfig {

    public static final String VARSLING_KEY = "start.varsling.withmock";

    @Bean
    public VarslerPorttype varslerPortType() {
        final VarslerPorttype prod = createVarslingPortType(new UserSAMLOutInterceptor());
        final VarslerPorttype mock = new VarslerMock();

        return TimingMetricsProxy.createMetricsProxyWithInstanceSwitcher(prod, mock, VARSLING_KEY, VarslerPorttype.class);
    }

    @Bean
    public Pingable varslerPing() {
        final VarslerPorttype ws = createVarslingPortType(new SystemSAMLOutInterceptor());
        return new PingableWebService("Varsler", ws);
    }


    private static VarslerPorttype createVarslingPortType(AbstractSAMLOutInterceptor interceptor) {
        return new CXFClient<>(VarslerPorttype.class)
                .wsdl("classpath:Varsler.wsdl")
                .address(System.getProperty("varsler.ws.url"))
                .withOutInterceptor(interceptor)
                .build();
    }
}

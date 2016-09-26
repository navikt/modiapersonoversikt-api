package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.henvendelsesoknader;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelseSoknaderPortTypeMock;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;

@Configuration
public class HenvendelseSoknaderEndpointConfig {

    public static final String HENVENDELSESOKNADER_KEY = "start.henvendelsesoknader.withmock";

    @Bean
    public HenvendelseSoknaderPortType henvendelseSoknaderPortType() {
        final HenvendelseSoknaderPortType prod = createHenvendelsePortType(new UserSAMLOutInterceptor());
        final HenvendelseSoknaderPortType mock = new HenvendelseSoknaderPortTypeMock().getHenvendelseSoknaderPortTypeMock();

        return createMetricsProxyWithInstanceSwitcher("Henvendelsesoknader_v1", prod, mock, HENVENDELSESOKNADER_KEY, HenvendelseSoknaderPortType.class);
    }

    @Bean
    public Pingable pingHenvendelseSoknader() {
        final HenvendelseSoknaderPortType ws = createHenvendelsePortType(new SystemSAMLOutInterceptor());
        return new PingableWebService("Henvendelse soknader", ws);
    }

    private HenvendelseSoknaderPortType createHenvendelsePortType(AbstractSAMLOutInterceptor interceptor) {
        return new CXFClient<>(HenvendelseSoknaderPortType.class)
                .timeout(15000, 15000)
                .wsdl("classpath:no/nav/tjeneste/domene/brukerdialog/henvendelsesoknader/v1/Soknader.wsdl")
                .address(System.getProperty("henvendelser.ws.url"))
                .withOutInterceptor(interceptor)
                .build();
    }

}

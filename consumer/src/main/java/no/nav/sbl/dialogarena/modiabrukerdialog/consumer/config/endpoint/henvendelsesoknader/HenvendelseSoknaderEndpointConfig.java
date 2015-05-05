package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.henvendelsesoknader;

import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelseSoknaderPortTypeMock;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.InstanceSwitcher.createSwitcher;

@Configuration
public class HenvendelseSoknaderEndpointConfig {

    public static final String HENVENDELSESOKNADER_KEY = "start.henvendelsesoknader.withmock";

    @Bean
    public HenvendelseSoknaderPortType henvendelseSoknaderPortType() {
        final HenvendelseSoknaderPortType mock = new HenvendelseSoknaderPortTypeMock().getHenvendelseSoknaderPortTypeMock();
        final HenvendelseSoknaderPortType prod = createHenvendelsePortType(new UserSAMLOutInterceptor());
        return createSwitcher(prod, mock, HENVENDELSESOKNADER_KEY, HenvendelseSoknaderPortType.class);
    }

    @Bean
    public Pingable pingHenvendelseSoknader() {
        return new Pingable() {
            @Override
            public List<PingResult> ping() {

                long start = currentTimeMillis();
                String name = "HENVENDELSE_SOKNADER";
                try {
                    createHenvendelsePortType(new SystemSAMLOutInterceptor()).ping();
                    return asList(new PingResult(name, SERVICE_OK, currentTimeMillis() - start));
                } catch (Exception e) {
                    return asList(new PingResult(name, SERVICE_FAIL, currentTimeMillis() - start));
                }
            }
        };
    }

    private HenvendelseSoknaderPortType createHenvendelsePortType(AbstractSAMLOutInterceptor interceptor) {
        return new CXFClient<>(HenvendelseSoknaderPortType.class)
                .wsdl("classpath:no/nav/tjeneste/domene/brukerdialog/henvendelsesoknader/v1/Soknader.wsdl")
                .address(System.getProperty("henvendelser.ws.url"))
                .withOutInterceptor(interceptor)
                .build();
    }

}

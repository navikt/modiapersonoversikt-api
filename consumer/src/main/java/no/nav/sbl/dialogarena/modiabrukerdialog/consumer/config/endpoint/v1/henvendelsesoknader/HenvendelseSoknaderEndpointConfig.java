package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.henvendelsesoknader;

import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.sbl.util.EnvironmentUtils;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class HenvendelseSoknaderEndpointConfig {

    @Bean
    public HenvendelseSoknaderPortType henvendelseSoknaderPortType() {
        final HenvendelseSoknaderPortType prod = createHenvendelsePortType().configureStsForSubject().build();

        return createTimerProxyForWebService("Henvendelsesoknader_v1", prod, HenvendelseSoknaderPortType.class);
    }

    @Bean
    public Pingable pingHenvendelseSoknader() {
        final HenvendelseSoknaderPortType ws = createHenvendelsePortType().configureStsForSystemUser().build();
        return new PingableWebService("Henvendelse soknader", ws);
    }

    private CXFClient<HenvendelseSoknaderPortType> createHenvendelsePortType() {
        return new CXFClient<>(HenvendelseSoknaderPortType.class)
                .timeout(15000, 15000)
//                .wsdl("classpath:no/nav/tjeneste/domene/brukerdialog/henvendelsesoknader/v1/Soknader.wsdl")
                .address(EnvironmentUtils.getRequiredProperty("DOMENE_BRUKERDIALOG_HENVENDELSESOKNADERSERVICE_V1_ENDPOINTURL"));
    }

}

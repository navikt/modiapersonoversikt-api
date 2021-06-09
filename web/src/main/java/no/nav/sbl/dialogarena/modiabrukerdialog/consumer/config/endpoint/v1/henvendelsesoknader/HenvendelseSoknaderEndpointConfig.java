package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.henvendelsesoknader;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Autowired;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class HenvendelseSoknaderEndpointConfig {
    @Autowired
    private StsConfig stsConfig;

    @Bean
    public HenvendelseSoknaderPortType henvendelseSoknaderPortType() {
        final HenvendelseSoknaderPortType prod = createHenvendelsePortType().configureStsForSubject(stsConfig).build();

        return createTimerProxyForWebService("Henvendelsesoknader_v1", prod, HenvendelseSoknaderPortType.class);
    }

    @Bean
    public Pingable pingHenvendelseSoknader() {
        final HenvendelseSoknaderPortType ws = createHenvendelsePortType().configureStsForSystemUser(stsConfig).build();
        return new PingableWebService("Henvendelse soknader", ws);
    }

    private CXFClient<HenvendelseSoknaderPortType> createHenvendelsePortType() {
        return new CXFClient<>(HenvendelseSoknaderPortType.class)
                .timeout(15000, 15000)
//                .wsdl("classpath:no/nav/tjeneste/domene/brukerdialog/henvendelsesoknader/v1/Soknader.wsdl")
                .address(EnvironmentUtils.getRequiredProperty("DOMENE_BRUKERDIALOG_HENVENDELSESOKNADERSERVICE_V1_ENDPOINTURL"));
    }

}

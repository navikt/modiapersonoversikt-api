package no.nav.kjerneinfo.consumer.organisasjon;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.tjeneste.virksomhet.organisasjon.v4.OrganisasjonV4;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class OrganisasjonV4ConsumerConfig {

    public static final String ORGANISASJON_V4_ENDPOINT_KEY = "VIRKSOMHET_ORGANISASJON_V4_ENDPOINTURL";

    @Inject
    private StsConfig stsConfig;

    @Bean
    public OrganisasjonV4 organisasjonV4PortType() {
        OrganisasjonV4 portType = lagEndpoint().configureStsForSubject(stsConfig).build();
        return createTimerProxyForWebService("Organisasjon_v4", portType, OrganisasjonV4.class);
    }

    private CXFClient<OrganisasjonV4> lagEndpoint() {
        return new CXFClient<>(OrganisasjonV4.class)
                .address(EnvironmentUtils.getRequiredProperty(ORGANISASJON_V4_ENDPOINT_KEY));
    }

    @Bean
    public Pingable organisasjonPingable() {
        return new PingableWebService("Organisasjon", lagEndpoint().configureStsForSystemUser(stsConfig).build());
    }

}

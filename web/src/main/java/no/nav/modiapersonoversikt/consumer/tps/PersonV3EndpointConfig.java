package no.nav.modiapersonoversikt.consumer.tps;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.modiapersonoversikt.infrastructure.ping.PingableWebService;
import no.nav.modiapersonoversikt.infrastructure.types.Pingable;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Autowired;

import static no.nav.modiapersonoversikt.infrastructure.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class PersonV3EndpointConfig {

    @Autowired
    private StsConfig stsConfig;

    @Bean
    public PersonV3 personV3() {
        final PersonV3 personV3 = lagEndpoint().configureStsForSubject(stsConfig).build();

        return createTimerProxyForWebService("personV3", personV3, PersonV3.class);
    }

    @Bean
    public Pingable personPing() {
        return new PingableWebService("TPS - PersonV3", lagEndpoint().configureStsForSystemUser(stsConfig).build());
    }

    private CXFClient<PersonV3> lagEndpoint() {
        return new CXFClient<>(PersonV3.class)
                .address(EnvironmentUtils.getRequiredProperty("VIRKSOMHET_PERSON_V3_ENDPOINTURL"));
    }

}

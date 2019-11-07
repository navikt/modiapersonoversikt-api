package no.nav.kjerneinfo.consumer.fim.person.config;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.util.EnvironmentUtils;
import no.nav.tjeneste.virksomhet.person.v3.PersonV3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class PersonV3EndpointConfig {

    @Bean
    public PersonV3 personV3() {
        final PersonV3 personV3 = lagEndpoint().configureStsForSubject().build();

        return createTimerProxyForWebService("personV3", personV3, PersonV3.class);
    }

    @Bean
    public Pingable personPing() {
        return new PingableWebService("TPS - PersonV3", lagEndpoint().configureStsForSystemUser().build());
    }

    private CXFClient<PersonV3> lagEndpoint() {
        return new CXFClient<>(PersonV3.class)
                .address(EnvironmentUtils.getRequiredProperty("VIRKSOMHET_PERSON_V3_ENDPOINTURL"));
    }

}

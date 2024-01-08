package no.nav.modiapersonoversikt.consumer.tps;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.modiapersonoversikt.infrastructure.ping.PingableWebService;
import no.nav.modiapersonoversikt.infrastructure.ping.Pingable;
import no.nav.modiapersonoversikt.service.unleash.Feature;
import no.nav.modiapersonoversikt.service.unleash.UnleashService;
import no.nav.tjeneste.virksomhet.personsoek.v1.PersonsokPortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import javax.xml.namespace.QName;

import static no.nav.modiapersonoversikt.infrastructure.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class PersonsokEndpointConfig {

    @Autowired
    private StsConfig stsConfig;

    @Autowired
    private UnleashService unleashService;

    @Bean
    public PersonsokPortType personsokPortType() {
        final PersonsokPortType prod = createPersonsokPortType().configureStsForSubject(stsConfig).build();

        return createTimerProxyForWebService("PersonsokV1", prod, PersonsokPortType.class);
    }

    @Bean
    public Pingable personsokPing() {
        final PersonsokPortType ws = createPersonsokPortType().configureStsForSystemUser(stsConfig).build();

        return new PingableWebService("Personsok", ws);
    }

    private CXFClient<PersonsokPortType> createPersonsokPortType() {
        String adress;

        if (unleashService.isEnabled(Feature.NY_TPSWS_INGRESS)) {
            adress = "VIRKSOMHET_PERSONSOK_V1_ENDPOINTURL_NAIS";
        } else {
            adress = "VIRKSOMHET_PERSONSOK_V1_ENDPOINTURL";
        }

        return new CXFClient<>(PersonsokPortType.class)
                .wsdl("classpath:wsdl/no/nav/tjeneste/virksomhet/personsoek/v1/personsoek.wsdl")
                .serviceName(new QName("http://nav.no/tjeneste/virksomhet/personsoek/v1/", "Personsok_v1"))
                .endpointName(new QName("http://nav.no/tjeneste/virksomhet/personsoek/v1/", "Personsok_v1"))
                .address(EnvironmentUtils.getRequiredProperty(adress));
    }

}

package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.personsok;

import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.sbl.util.EnvironmentUtils;
import no.nav.tjeneste.virksomhet.personsoek.v1.PersonsokPortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.QName;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class PersonsokEndpointConfig {

    @Bean
    public PersonsokPortType personsokPortType() {
        final PersonsokPortType prod = createPersonsokPortType().configureStsForSubject().build();

        return createTimerProxyForWebService("PersonsokV1", prod, PersonsokPortType.class);
    }

    @Bean
    public Pingable personsokPing() {
        final PersonsokPortType ws = createPersonsokPortType().configureStsForSystemUser().build();

        return new PingableWebService("Personsok", ws);
    }

    private CXFClient<PersonsokPortType> createPersonsokPortType() {
        return new CXFClient<>(PersonsokPortType.class)
                .wsdl("classpath:wsdl/no/nav/tjeneste/virksomhet/personsoek/v1/Personsoek.wsdl")
                .serviceName(new QName("http://nav.no/tjeneste/virksomhet/personsoek/v1/", "Personsok_v1"))
                .endpointName(new QName("http://nav.no/tjeneste/virksomhet/personsoek/v1/", "Personsok_v1"))
                .address(EnvironmentUtils.getRequiredProperty("VIRKSOMHET_PERSONSOK_V1_ENDPOINTURL"));
    }

}

package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.personsok;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.PersonsokPortTypeMock;
import no.nav.tjeneste.virksomhet.personsoek.v1.PersonsokPortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.QName;

import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;

@Configuration
public class PersonsokEndpointConfig {

    public static final String PERSONSOK_KEY = "start.personsok.withmock";

    @Bean
    public PersonsokPortType personsokPortType() {
        final PersonsokPortType prod = createPersonsokPortType().configureStsForSubject().build();
        final PersonsokPortType mock = PersonsokPortTypeMock.createPersonsokMock();

        return createMetricsProxyWithInstanceSwitcher("PersonsokV1", prod, mock, PERSONSOK_KEY, PersonsokPortType.class);
    }

    @Bean
    public Pingable personsokPing() {
        final PersonsokPortType ws = createPersonsokPortType().configureStsForSystemUser().build();

        return new PingableWebService("Personsok", ws);
    }

    private CXFClient<PersonsokPortType> createPersonsokPortType() {
        return new CXFClient<>(PersonsokPortType.class)
//                .wsdl("classpath:no/nav/tjeneste/virksomhet/personsoek/v1/Personsoek.wsdl")
//                .serviceName(new QName("http://nav.no/tjeneste/virksomhet/personsoek/v1/", "Personsok_v1"))
                .address(System.getProperty("personsokendpoint.url"));
    }

}

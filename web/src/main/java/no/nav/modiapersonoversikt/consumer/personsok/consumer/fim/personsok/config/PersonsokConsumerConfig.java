package no.nav.modiapersonoversikt.consumer.personsok.consumer.fim.personsok.config;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.modiapersonoversikt.infrastructure.jaxws.handlers.MDCOutHandler;
import no.nav.modiapersonoversikt.infrastructure.ping.PingableWebService;
import no.nav.modiapersonoversikt.infrastructure.types.Pingable;
import no.nav.tjeneste.virksomhet.personsoek.v1.PersonsokPortType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import javax.xml.namespace.QName;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

@Configuration
public class PersonsokConsumerConfig {
    @Value("${VIRKSOMHET_PERSONSOK_V1_ENDPOINTURL:}")
    private String personsokEndpointUrl;
    @Value("${servicegateway.url:}")
    private String servicegateway;

    //@Autowired
    StsConfig stsConfig;

    //@Bean
    public PersonsokPortType personsokPortType() {
        return getHentPersonsokJaxWsPortProxyFactoryBean()
                .configureStsForSubject(stsConfig)
                .build();
    }

    //@Bean
    public Pingable personsokPing() {
        PersonsokPortType pingPorttype = getHentPersonsokJaxWsPortProxyFactoryBean()
                .configureStsForSystemUser(stsConfig)
                .build();
        return new PingableWebService("Personsok", pingPorttype);
    }

    private CXFClient<PersonsokPortType> getHentPersonsokJaxWsPortProxyFactoryBean() {
        return new CXFClient<>(PersonsokPortType.class)
                .wsdl("classpath:no/nav/tjeneste/virksomhet/personsoek/v1/Personsoek.wsdl")
                .serviceName(new QName("http://nav.no/tjeneste/virksomhet/personsoek/v1/", "Personsok_v1"))
                .address(getAddress())
                .withHandler(new MDCOutHandler());
    }

    private String getAddress() {
        return defaultIfBlank(servicegateway, personsokEndpointUrl);
    }
}

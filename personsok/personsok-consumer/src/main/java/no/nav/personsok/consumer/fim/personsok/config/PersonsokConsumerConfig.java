package no.nav.personsok.consumer.fim.personsok.config;

import no.nav.modig.jaxws.handlers.MDCOutHandler;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.personsoek.v1.PersonsokPortType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.QName;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

@Configuration
public class PersonsokConsumerConfig {
    @Value("${personsokendpoint.url:}")
    private String personsokEndpointUrl;
    @Value("${servicegateway.url:}")
    private String servicegateway;

    @Bean
    public PersonsokPortType personsokPortType() {
        return getHentPersonsokJaxWsPortProxyFactoryBean()
                .configureStsForSubject()
                .build();
    }

    @Bean
    public Pingable personsokPing() {
        PersonsokPortType pingPorttype = getHentPersonsokJaxWsPortProxyFactoryBean().configureStsForSystemUser().build();
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

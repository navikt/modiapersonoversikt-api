package no.nav.modiapersonoversikt.integration.personsok.consumer.config;

import no.nav.modiapersonoversikt.integration.personsok.consumer.fim.kodeverk.KodeverkManager;
import no.nav.modiapersonoversikt.integration.personsok.consumer.fim.kodeverk.support.DefaultKodeverkManager;
import no.nav.modiapersonoversikt.integration.personsok.consumer.fim.kodeverk.support.KodeverkServiceDelegate;
import no.nav.modiapersonoversikt.integration.personsok.consumer.fim.mapping.FIMMapper;
import no.nav.modiapersonoversikt.integration.personsok.consumer.fim.personsok.PersonsokServiceBi;
import no.nav.modiapersonoversikt.integration.personsok.consumer.fim.personsok.support.DefaultPersonsokService;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.personsoek.v1.PersonsokPortType;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableCaching
public class ConsumerConfig {

    @Autowired
    private PersonsokPortType personsokPortType;

    @Autowired
    private KodeverkPortType kodeverkPortType;

    @Bean
    public PersonsokServiceBi personsokServiceBi() {
        DefaultPersonsokService personsokService = new DefaultPersonsokService();
        personsokService.setPersonsokService(personsokPortType);
        personsokService.setMapper(fimMapper());
        return personsokService;
    }


    @Bean
    public FIMMapper fimMapper() {
        return new FIMMapper(kodeverkManager());
    }

    @Bean
    public KodeverkManager kodeverkManager() {
        return new DefaultKodeverkManager(kodeverkServiceDelegate());
    }

    @Bean
    public KodeverkServiceDelegate kodeverkServiceDelegate() {
        KodeverkServiceDelegate delegate = new KodeverkServiceDelegate();
        delegate.setKodeverkPortType(kodeverkPortType);
        return delegate;
    }
}

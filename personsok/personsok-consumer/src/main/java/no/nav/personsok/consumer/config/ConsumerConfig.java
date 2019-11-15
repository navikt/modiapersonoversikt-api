package no.nav.personsok.consumer.config;

import no.nav.personsok.consumer.fim.kodeverk.KodeverkManager;
import no.nav.personsok.consumer.fim.kodeverk.support.DefaultKodeverkManager;
import no.nav.personsok.consumer.fim.kodeverk.support.KodeverkServiceDelegate;
import no.nav.personsok.consumer.fim.mapping.FIMMapper;
import no.nav.personsok.consumer.fim.personsok.PersonsokServiceBi;
import no.nav.personsok.consumer.fim.personsok.support.DefaultPersonsokService;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.personsoek.v1.PersonsokPortType;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

@Configuration
@EnableCaching
public class ConsumerConfig {

    @Inject
    private PersonsokPortType personsokPortType;

    @Inject
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

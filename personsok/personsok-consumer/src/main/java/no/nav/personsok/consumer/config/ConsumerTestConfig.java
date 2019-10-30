package no.nav.personsok.consumer.config;

import no.nav.personsok.consumer.fim.kodeverk.KodeverkManager;
import no.nav.personsok.consumer.fim.kodeverk.support.MockKodeverkManager;
import no.nav.personsok.consumer.fim.mapping.FIMMapper;
import no.nav.personsok.consumer.fim.personsok.PersonsokServiceBi;
import no.nav.personsok.consumer.fim.personsok.mock.PersonsokServiceMock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsumerTestConfig {

    @Bean
    public PersonsokServiceBi personsokServiceBi() {
        PersonsokServiceMock personsokService = new PersonsokServiceMock();
        personsokService.setMapper(fimMapper());
        return personsokService;
    }

    private FIMMapper fimMapper() {
        return new FIMMapper(kodeverkManager());
    }

    private KodeverkManager kodeverkManager() {
        return new MockKodeverkManager();
    }
}
package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.personsok;

import no.nav.personsok.consumer.fim.kodeverk.KodeverkManager;
import no.nav.personsok.consumer.fim.kodeverk.support.DefaultKodeverkManager;
import no.nav.personsok.consumer.fim.kodeverk.support.KodeverkServiceDelegate;
import no.nav.personsok.consumer.fim.mapping.FIMMapper;
import no.nav.personsok.consumer.fim.personsok.PersonsokServiceBi;
import no.nav.personsok.consumer.fim.personsok.support.DefaultPersonsokService;
import no.nav.personsok.consumer.utils.ping.PersonsokPing;
import no.nav.tjeneste.virksomhet.kodeverk.v1.KodeverkPortType;
import no.nav.tjeneste.virksomhet.personsoek.v1.PersonsokPortType;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import javax.inject.Named;

import static org.mockito.Mockito.mock;

@Configuration
@EnableCaching
public class ConsumerConfigMock {

//    @Inject
//    @Named("personsokPortType")
    private PersonsokPortType personsokPortType;

//    @Inject
//    @Named("selftestPersonsokPortType")
    private PersonsokPortType selftestPersonsokPortType;

//    @Inject
    private KodeverkPortType kodeverkPortType;

    public ConsumerConfigMock() {
        kodeverkPortType = mock(KodeverkPortType.class);
        selftestPersonsokPortType = mock(PersonsokPortType.class);
        personsokPortType = mock(PersonsokPortType.class);
    }

    @Bean
    public PersonsokServiceBi personsokServiceBi() {
        DefaultPersonsokService personsokService = new DefaultPersonsokService();
        personsokService.setPersonsokService(personsokPortType);
        personsokService.setSelftestPersonsokService(selftestPersonsokPortType);
        personsokService.setMapper(fimMapper());
        return personsokService;
    }

    @Bean
    public PersonsokPing personsokPing() {
        return new PersonsokPing(personsokServiceBi());
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

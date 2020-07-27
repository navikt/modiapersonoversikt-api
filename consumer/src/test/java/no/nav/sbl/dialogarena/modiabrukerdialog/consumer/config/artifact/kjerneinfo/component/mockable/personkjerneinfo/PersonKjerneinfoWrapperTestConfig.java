package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.personkjerneinfo;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class PersonKjerneinfoWrapperTestConfig {

    @Bean
    public PersonKjerneinfoServiceBi personKjerneinfoServiceDefault() {
        return mock(PersonKjerneinfoServiceBi.class);
    }
}


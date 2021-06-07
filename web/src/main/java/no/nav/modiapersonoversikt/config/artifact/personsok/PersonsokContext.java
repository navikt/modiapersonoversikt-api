package no.nav.modiapersonoversikt.config.artifact.personsok;

import no.nav.modiapersonoversikt.integration.personsok.consumer.config.PersonsokServiceConsumerConfig;
import no.nav.modiapersonoversikt.integration.personsok.consumer.fim.personsok.config.PersonsokConsumerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        PersonsokServiceConsumerConfig.class,
        PersonsokConsumerConfig.class,
})
public class PersonsokContext {


}

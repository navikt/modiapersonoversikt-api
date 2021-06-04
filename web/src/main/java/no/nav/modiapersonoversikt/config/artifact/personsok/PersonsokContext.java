package no.nav.modiapersonoversikt.config.artifact.personsok;

import no.nav.modiapersonoversikt.integration.personsok.consumer.config.ConsumerConfig;
import no.nav.modiapersonoversikt.integration.personsok.consumer.fim.personsok.config.PersonsokConsumerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        PersonsokConsumerConfig.class,
        ConsumerConfig.class,
})
public class PersonsokContext {


}

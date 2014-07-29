package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.personsok;

import no.nav.personsok.config.spring.SecurityPolicyConfig;
import no.nav.personsok.consumer.config.ConsumerConfig;
import no.nav.personsok.consumer.fim.personsok.config.PersonsokConsumerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        PersonsokConsumerConfig.class,
        ConsumerConfig.class,
        SecurityPolicyConfig.class
})
public class PersonsokContext {


}

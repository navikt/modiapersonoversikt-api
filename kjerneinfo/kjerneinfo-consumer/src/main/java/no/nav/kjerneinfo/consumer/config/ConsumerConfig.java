package no.nav.kjerneinfo.consumer.config;

import no.nav.kjerneinfo.consumer.fim.person.config.PersonKjerneinfoMapperConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({PersonKjerneinfoMapperConfig.class})
public class ConsumerConfig {

}

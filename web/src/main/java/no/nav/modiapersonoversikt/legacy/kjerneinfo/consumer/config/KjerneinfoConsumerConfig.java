package no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.config;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.config.PersonKjerneinfoMapperConfig;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.config.PersonV3EndpointConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({PersonV3EndpointConfig.class, PersonKjerneinfoMapperConfig.class})
public class KjerneinfoConsumerConfig {

}

package no.nav.kjerneinfo.consumer.config;

import no.nav.kjerneinfo.consumer.fim.behandleperson.config.BehandlePersonEndpointConfig;
import no.nav.kjerneinfo.consumer.fim.person.config.PersonKjerneinfoMapperConfig;
import no.nav.kjerneinfo.consumer.fim.person.config.PersonV3EndpointConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({PersonV3EndpointConfig.class, PersonKjerneinfoMapperConfig.class, BehandlePersonEndpointConfig.class})
public class ConsumerConfig {

}

package no.nav.modiapersonoversikt.config;

import no.nav.modiapersonoversikt.config.artifact.kjerneinfo.KjerneinfoContext;
import no.nav.modiapersonoversikt.config.endpoint.EndpointsConfig;
import no.nav.modiapersonoversikt.config.service.ServiceConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        KjerneinfoContext.class,
        EndpointsConfig.class,
        ServiceConfig.class
})
public class ConsumerContext {
}

package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.ArtifactsConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.EndpointsConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.service.ServiceConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        ArtifactsConfig.class,
        EndpointsConfig.class,
        ServiceConfig.class
})
public class ConsumerContext {
}

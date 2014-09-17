package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.ArtifactsConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.EndpointsConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.service.ServiceConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.slf4j.bridge.SLF4JBridgeHandler.install;
import static org.slf4j.bridge.SLF4JBridgeHandler.removeHandlersForRootLogger;

@Configuration
@Import({
        ArtifactsConfig.class,
        EndpointsConfig.class,
        ServiceConfig.class
})
public class ConsumerContext {

    static {
        // Sikkerhetsrammeverkene logger til java.util.logging
        removeHandlersForRootLogger();
        install();
    }

}

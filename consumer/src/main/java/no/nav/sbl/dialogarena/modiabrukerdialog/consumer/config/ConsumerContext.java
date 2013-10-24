package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.ArtifactsConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.EndpointsConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.ServicesConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.slf4j.bridge.SLF4JBridgeHandler.install;
import static org.slf4j.bridge.SLF4JBridgeHandler.removeHandlersForRootLogger;

@Configuration
@Import({
        ArtifactsConfig.class,
        EndpointsConfig.class,
        ServicesConfig.class
})
public class ConsumerContext {

    static {
        // Sikkerhetsrammeverkene logger til java.util.logging
        removeHandlersForRootLogger();
        install();
    }

}

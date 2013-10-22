package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.artifacts.ArtifactsConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.endpoints.EndpointsConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.services.ServicesConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.slf4j.bridge.SLF4JBridgeHandler.install;
import static org.slf4j.bridge.SLF4JBridgeHandler.removeHandlersForRootLogger;

@Configuration
@Import(value = {
        ArtifactsConfig.class,
        EndpointsConfig.class,
        ServicesConfig.class
})
public class ComponentsContext {

    static {
        // Sikkerhetsrammeverkene logger til java.util.logging
        removeHandlersForRootLogger();
        install();
    }
}

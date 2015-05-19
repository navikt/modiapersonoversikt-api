package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.config.ApplicationContextProviderConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.ArtifactsConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.EndpointsConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.service.ServiceConfig;
import no.nav.modig.modia.metrics.MetricsConfigurator;
import no.nav.sbl.modiabrukerdialog.pip.geografisk.config.GeografiskPipConfig;
import no.nav.sbl.modiabrukerdialog.pip.journalforing.config.JournalfortTemaPipConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.slf4j.bridge.SLF4JBridgeHandler.install;
import static org.slf4j.bridge.SLF4JBridgeHandler.removeHandlersForRootLogger;

@Configuration
@Import({
        ApplicationContextProviderConfig.class,
        ArtifactsConfig.class,
        EndpointsConfig.class,
        ServiceConfig.class,
        JournalfortTemaPipConfig.class,
        GeografiskPipConfig.class
})
public class ConsumerContext {
    @Bean
    public MetricsConfigurator metricsConfigurator() {
        return new MetricsConfigurator();
    }

    static {
        // Sikkerhetsrammeverkene logger til java.util.logging
        removeHandlersForRootLogger();
        install();
    }

}

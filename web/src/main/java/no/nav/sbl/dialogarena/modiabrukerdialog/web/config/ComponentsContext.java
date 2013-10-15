package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.tjenester.HenvendelseinnsynConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.tjenester.SoknaderConfig;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {
        OldApplicationsContext.class,
        HenvendelseinnsynConfig.Default.class,
        OppgavebehandlingConfig.Default.class,
        SoknaderConfig.Default.class,
})
public class ComponentsContext {

    static {
        // Sikkerhetsrammeverkene logger til java.util.logging
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }
}

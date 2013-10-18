package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.tjenester.BesvareHenvendelseTjenesteConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.tjenester.HenvendelseTjenesteConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.tjenester.SakOgBehandlingTjenesteConfig;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {
        OldApplicationsContext.class,
        HenvendelseTjenesteConfig.Default.class,
        BesvareHenvendelseTjenesteConfig.Default.class,
        OppgavebehandlingConfig.Default.class,
        SakOgBehandlingTjenesteConfig.Default.class,
})
public class ComponentsContext {

    static {
        // Sikkerhetsrammeverkene logger til java.util.logging
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }
}

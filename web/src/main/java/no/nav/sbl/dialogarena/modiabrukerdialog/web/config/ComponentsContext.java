package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.felles.SoknaderConfig;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {
        no.nav.sykmeldingsperioder.config.SykmeldingsperioderPanelConfig.class,
        no.nav.personsok.config.spring.PersonsokConfig.class,
        no.nav.kjerneinfo.config.spring.KjerneinfoPanelConfig.class,
        no.nav.kjerneinfo.kontrakter.config.KontrakterPanelConfig.class,
        no.nav.brukerprofil.config.BrukerprofilPanelConfig.class,
        no.nav.sbl.dialogarena.sporsmalogsvar.config.BesvareHenvendelseConfig.class,
        no.nav.sbl.dialogarena.modiabrukerdialog.web.config.felles.HenvendelseinnsynConfig.class,
        no.nav.sbl.dialogarena.sporsmalogsvar.config.BesvareServiceConfig.class,
        OppgavebehandlingConfig.class,
        SoknaderConfig.class,
})
public class ComponentsContext {

    static {
        // Sikkerhetsrammeverkene logger til java.util.logging
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }
}

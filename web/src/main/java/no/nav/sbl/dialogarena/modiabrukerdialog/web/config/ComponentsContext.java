package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {
        no.nav.sykmeldingsperioder.config.SykmeldingsperioderPanelConfig.class
        , no.nav.personsok.config.spring.PersonsokConfig.class
        , no.nav.kjerneinfo.config.spring.KjerneinfoPanelConfig.class
        , no.nav.kjerneinfo.kontrakter.config.KontrakterPanelConfig.class
        , no.nav.brukerprofil.config.BrukerprofilPanelConfig.class
        , no.nav.sbl.dialogarena.besvare.config.BesvareSporsmalConfig.class
})
public class ComponentsContext {

    static {
        // Sikkerhetsrammeverkene logger til java.util.logging
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }
}

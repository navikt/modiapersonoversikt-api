package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {
        no.nav.sykmeldingsperioder.config.SykmeldingsperioderPanelConfig.class
        , no.nav.personsok.config.spring.PersonsokConfig.class
        , no.nav.kjerneinfo.config.spring.KjerneinfoPanelConfig.class
        , no.nav.kjerneinfo.kontrakter.config.KontrakterPanelConfig.class
        , no.nav.brukerprofil.config.BrukerprofilPanelConfig.class
})
public class ComponentsContext {

}

package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Gamle applikasjoner definerer egne kontekstklasser. MODIA importerer disse heller enn å lage sine egne
 */
@Configuration
@Import(value = {
        PersonsokContext.class,
        no.nav.sykmeldingsperioder.config.SykmeldingsperioderPanelConfig.class,
        no.nav.kjerneinfo.config.spring.KjerneinfoPanelConfig.class,
        no.nav.kjerneinfo.kontrakter.config.KontrakterPanelConfig.class,
        no.nav.brukerprofil.config.BrukerprofilPanelConfig.class,
})
public class OldApplicationsContext {

}

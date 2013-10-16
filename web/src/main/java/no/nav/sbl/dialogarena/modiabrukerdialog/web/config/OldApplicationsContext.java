package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Gamle applikasjoner bruker profiler for å styre springkonteksten. For å
 * skjerme resten av MODIA fra dette blir de wiret opp sammen i denne kontekstklassen
 */
@Configuration
@Import(value = {
        no.nav.sykmeldingsperioder.config.SykmeldingsperioderPanelConfig.class,
        no.nav.personsok.config.spring.PersonsokConfig.class,
        no.nav.kjerneinfo.config.spring.KjerneinfoPanelConfig.class,
        no.nav.kjerneinfo.kontrakter.config.KontrakterPanelConfig.class,
        no.nav.brukerprofil.config.BrukerprofilPanelConfig.class,
})
public class OldApplicationsContext {

}

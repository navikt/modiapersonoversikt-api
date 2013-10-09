package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static java.lang.System.setProperty;

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
        no.nav.sbl.dialogarena.sporsmalogsvar.config.BesvareHenvendelseConfig.class,
        no.nav.sbl.dialogarena.sporsmalogsvar.config.BesvareServiceConfig.class
})
public class OldApplicationsContext {

    static {
        setProperty("spring.profiles.active", "default");
        setProperty("spring.profiles.active", "kjerneinfoDefault");
        setProperty("spring.profiles.active", "sykemeldingsperioderDefault");
        setProperty("spring.profiles.active", "kontrakterDefault");
        setProperty("spring.profiles.active", "personsokDefault");
        setProperty("spring.profiles.active", "brukerprofilDefault");
        setProperty("spring.profiles.active", "behandleBrukerprofilDefault");
        setProperty("spring.profiles.active", "brukerhenvendelserDefault");
        setProperty("spring.profiles.active", "oppgavebehandlingDefault");
    }

}

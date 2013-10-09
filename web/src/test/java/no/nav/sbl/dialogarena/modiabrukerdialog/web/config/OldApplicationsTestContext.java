package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.sbl.dialogarena.sporsmalogsvar.config.BesvareServiceConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static java.lang.System.setProperty;

/**
 * @see OldApplicationsContext
 */
@Configuration
@Import(value = {
        no.nav.sykmeldingsperioder.config.SykmeldingsperioderPanelConfig.class,
        no.nav.personsok.config.spring.PersonsokConfig.class,
        no.nav.kjerneinfo.config.spring.KjerneinfoPanelConfig.class,
        no.nav.kjerneinfo.kontrakter.config.KontrakterPanelConfig.class,
        no.nav.brukerprofil.config.BrukerprofilPanelConfig.class,
        no.nav.sbl.dialogarena.sporsmalogsvar.config.BesvareHenvendelseConfig.class,
        BesvareServiceConfig.Default.class
})
public class OldApplicationsTestContext {

    static {
        setProperty("spring.profiles.active", "test");
    }

}

package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.felles.HenvendelseinnsynConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.felles.SoknaderConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {
        OldApplicationsTestContext.class,
        HenvendelseinnsynConfig.Test.class,
        OppgavebehandlingConfig.Test.class,
        SoknaderConfig.Test.class
})
public class ComponentsTestContext {

}

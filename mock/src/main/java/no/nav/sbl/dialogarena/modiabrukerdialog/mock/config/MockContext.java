package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config;

import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.Kjerneinfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelsePortTypeContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        Kjerneinfo.class,
        HenvendelsePortTypeContext.class
})
public class MockContext {


}

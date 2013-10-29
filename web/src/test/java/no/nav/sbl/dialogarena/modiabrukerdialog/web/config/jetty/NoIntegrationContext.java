package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.jetty;

import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.MockContextBeans;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.WicketApplicationBeans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        MockContextBeans.class,
        WicketApplicationBeans.class
})
public class NoIntegrationContext {

}

package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.artifacts.kjerneinfo.components;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        no.nav.brukerprofil.config.BrukerprofilPanelConfig.class
})
public class BrukerprofilPanelContext {
}

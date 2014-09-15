package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component;

import no.nav.brukerprofil.config.BrukerprofilPanelConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        BrukerprofilPanelConfig.class
})
public class BrukerprofilPanelContext {
}

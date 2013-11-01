package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components;

import no.nav.brukerprofil.config.BrukerprofilPanelConfig;
import no.nav.brukerprofil.config.BrukerprofilTilgangskontrollPolicyConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        BrukerprofilPanelConfig.class,
        BrukerprofilTilgangskontrollPolicyConfig.class
})
public class BrukerprofilPanelContext {
}

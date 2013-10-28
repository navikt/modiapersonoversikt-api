package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components;

import no.nav.brukerprofil.config.BrukerprofilPanelConfig;
import no.nav.brukerprofil.config.BrukerprofilTilgangskontrollPolicyConfig;
import no.nav.brukerprofil.config.spring.ConsumerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        BrukerprofilPanelConfig.class,
        ConsumerConfig.class,
        no.nav.behandlebrukerprofil.config.spring.ConsumerConfig.class,
        BrukerprofilTilgangskontrollPolicyConfig.class
})
public class BrukerprofilPanelContext {
}

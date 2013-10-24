package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components;

import no.nav.brukerprofil.config.spring.ConsumerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        no.nav.brukerprofil.config.BrukerprofilPanelConfig.class,
        ConsumerConfig.class,
        no.nav.behandlebrukerprofil.config.spring.ConsumerConfig.class,
        no.nav.brukerprofil.config.BrukerprofilTilgangskontrollPolicyConfig.class
})
public class BrukerprofilPanelContext {
}

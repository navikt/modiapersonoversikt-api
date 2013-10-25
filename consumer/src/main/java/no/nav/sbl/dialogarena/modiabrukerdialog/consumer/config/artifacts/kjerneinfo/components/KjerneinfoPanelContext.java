package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components;


import no.nav.kjerneinfo.config.spring.KjerneinfoPanelConfig;
import no.nav.kjerneinfo.consumer.config.ConsumerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        KjerneinfoPanelConfig.class,
        ConsumerConfig.class
})
public class KjerneinfoPanelContext {
}

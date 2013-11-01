package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components;


import no.nav.kjerneinfo.config.spring.KjerneinfoPanelConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        KjerneinfoPanelConfig.class,
})
public class KjerneinfoPanelContext {
}

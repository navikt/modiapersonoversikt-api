package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components;

import no.nav.kontrakter.consumer.config.ConsumerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        no.nav.kjerneinfo.kontrakter.config.KontrakterPanelConfig.class,
        ConsumerConfig.class
})
public class KontrakterPanelContext {
}

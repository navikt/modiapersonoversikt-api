package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components;

import no.nav.kjerneinfo.kontrakter.config.KontrakterPanelConfig;
import no.nav.kontrakter.consumer.config.ConsumerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        KontrakterPanelConfig.class,
        ConsumerConfig.class
})
public class KontrakterPanelContext {
}

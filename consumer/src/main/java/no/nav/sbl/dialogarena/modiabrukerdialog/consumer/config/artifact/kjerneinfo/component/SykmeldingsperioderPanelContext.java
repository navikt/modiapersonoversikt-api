package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component;

import no.nav.sykmeldingsperioder.config.spring.ConsumerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        ConsumerConfig.class
})
public class SykmeldingsperioderPanelContext {
}

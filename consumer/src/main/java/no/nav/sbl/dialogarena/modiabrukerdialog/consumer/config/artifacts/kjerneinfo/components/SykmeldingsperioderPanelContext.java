package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components;

import no.nav.sykmeldingsperioder.config.spring.ConsumerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        no.nav.sykmeldingsperioder.config.SykmeldingsperioderPanelConfig.class,
        ConsumerConfig.class
})
public class SykmeldingsperioderPanelContext {
}

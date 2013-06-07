package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        no.nav.dialogarena.modiabrukerdialog.example.config.ExampleContext.class,
        no.nav.kjerneinfo.consumer.config.ConsumerConfig.class,
        no.nav.sykmeldingsperioder.config.SykmeldingsperioderPanelConfig.class,
        no.nav.kjerneinfo.consumer.config.MockConsumerConfig.class
})
public class ComponentsContext {

}

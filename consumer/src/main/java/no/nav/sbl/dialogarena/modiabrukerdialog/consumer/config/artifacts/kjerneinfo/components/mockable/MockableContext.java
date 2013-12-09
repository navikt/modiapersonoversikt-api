package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        BrukerprofilConsumerConfigResolver.class,
        BehandleBrukerprofilConsumerConfigResolver.class,
        PersonKjerneinfoConsumerConfigResolver.class,
        PersonKjerneinfoMapperConfigResolver.class,
        SykmeldingsperioderPanelConfigResolver.class,
        KontrakterConsumerConfigResolver.class
})
public class MockableContext {

    public static final String KJERNEINFO_KEY = "start.kjerneinfo.withmock";

}

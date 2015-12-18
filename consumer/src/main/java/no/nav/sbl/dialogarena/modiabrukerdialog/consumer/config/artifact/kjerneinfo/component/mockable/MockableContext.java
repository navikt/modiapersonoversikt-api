package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.wrapper.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        BrukerprofilConsumerConfigResolver.class,
        BrukerprofilWrapper.class,

        BehandleBrukerprofilConsumerConfigResolver.class,
        BehandleBrukerprofilWrapper.class,

        DkifConsumerConfigResolver.class,
        DkifWrapper.class,

        KontrakterConsumerConfigResolver.class,
        KontrakterWrapper.class,

        KjerneinfoMapperConfigResolver.class,
        KjerneinfoMapperWrapper.class,

        PersonKjerneinfoConsumerConfigResolver.class,
        PersonKjerneinfoWrapper.class,

        SykmeldingsperioderPanelConfigResolver.class,
        SykmeldingsperioderWrapper.class
})
public class MockableContext {

    public static final String KJERNEINFO_KEY = "start.kjerneinfo.withmock";

}

package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.wrappers.BehandleBrukerprofilWrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.wrappers.BrukerprofilWrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.wrappers.KjerneinfoMapperWrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.wrappers.KontrakterWrapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        BrukerprofilConsumerConfigResolver.class,
        BrukerprofilWrapper.class,

        BehandleBrukerprofilConsumerConfigResolver.class,
        BehandleBrukerprofilWrapper.class,

        KontrakterConsumerConfigResolver.class,
        KontrakterWrapper.class,

        KjerneinfoMapperConfigResolver.class,
        KjerneinfoMapperWrapper.class,

        PersonKjerneinfoConsumerConfigResolver.class,
        SykmeldingsperioderPanelConfigResolver.class
})
public class MockableContext {

    public static final String KJERNEINFO_KEY = "start.kjerneinfo.withmock";

}

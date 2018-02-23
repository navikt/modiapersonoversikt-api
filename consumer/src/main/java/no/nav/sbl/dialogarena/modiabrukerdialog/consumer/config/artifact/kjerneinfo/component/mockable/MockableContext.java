package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable;

import no.nav.kjerneinfo.consumer.fim.behandleperson.config.BehandlePersonEndpointConfig;
import no.nav.kjerneinfo.consumer.fim.person.config.PersonV3EndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.wrapper.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        BehandleBrukerprofilConsumerConfigResolver.class,
        BehandleBrukerprofilWrapper.class,

        DkifConsumerConfigResolver.class,
        DkifWrapper.class,

        KontrakterConsumerConfigResolver.class,
        KontrakterWrapper.class,

        KjerneinfoMapperConfigResolver.class,
        KjerneinfoMapperWrapper.class,

        PersonV3EndpointConfig.class,
        KjerneinfoMapperConfigResolver.class,
        BehandlePersonEndpointConfig.class,

        SykmeldingsperioderPanelConfigResolver.class,
        SykmeldingsperioderWrapper.class,

        EgenAnsattConsumerConfigResolver.class,
        EgenAnsattWrapper.class
})
public class MockableContext {

    public static final String KJERNEINFO_KEY = "start.kjerneinfo.withmock";

}

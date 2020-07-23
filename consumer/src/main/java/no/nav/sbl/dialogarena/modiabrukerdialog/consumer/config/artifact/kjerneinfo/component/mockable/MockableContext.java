package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable;

import no.nav.dkif.config.spring.DkifConsumerConfig;
import no.nav.kjerneinfo.consumer.fim.person.config.PersonV3EndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.wrapper.EgenAnsattWrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.wrapper.KjerneinfoMapperWrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.wrapper.KontrakterWrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.wrapper.SykmeldingsperioderWrapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        KontrakterWrapper.class,
        DkifConsumerConfig.class,
        KjerneinfoMapperWrapper.class,
        PersonV3EndpointConfig.class,
        SykmeldingsperioderWrapper.class,
        EgenAnsattWrapper.class
})
public class MockableContext {
}

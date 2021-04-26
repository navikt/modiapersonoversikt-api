package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo;

import no.nav.dkif.config.spring.DkifConsumerConfig;
import no.nav.kjerneinfo.consumer.fim.person.config.PersonV3EndpointConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        KontrakterWrapper.class,
        DkifConsumerConfig.class,
        PersonV3EndpointConfig.class,
        EgenAnsattWrapper.class,
        no.nav.kjerneinfo.consumer.config.ConsumerConfig.class,
        no.nav.kodeverk.consumer.config.ConsumerConfig.class,
        no.nav.sykmeldingsperioder.config.spring.ConsumerConfig.class
})
public class KjerneinfoContext {
}

package no.nav.modiapersonoversikt.config.artifact.kjerneinfo;

import no.nav.modiapersonoversikt.config.DkifConsumerConfig;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.config.PersonV3EndpointConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        KontrakterWrapper.class,
        DkifConsumerConfig.class,
        PersonV3EndpointConfig.class,
        EgenAnsattWrapper.class,
        no.nav.modiapersonoversikt.consumer.kodeverk.consumer.config.KodeverkConsumerConfig.class,
        no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.config.spring.SykmeldingsperioderConsumerConfig.class
})
public class KjerneinfoContext {
}

package no.nav.modiapersonoversikt.config.artifact.kjerneinfo;

import no.nav.modiapersonoversikt.consumer.dkif.DkifConsumerConfig;
import no.nav.modiapersonoversikt.consumer.tps.PersonV3EndpointConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        KontrakterWrapper.class,
        DkifConsumerConfig.class,
        PersonV3EndpointConfig.class,
        EgenAnsattWrapper.class,
        no.nav.modiapersonoversikt.consumer.infotrygd.config.spring.SykmeldingsperioderConsumerConfig.class
})
public class KjerneinfoContext {
}

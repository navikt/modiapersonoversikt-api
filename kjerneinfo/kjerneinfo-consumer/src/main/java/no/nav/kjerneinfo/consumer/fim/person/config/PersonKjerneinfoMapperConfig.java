package no.nav.kjerneinfo.consumer.fim.person.config;

import no.nav.kjerneinfo.consumer.fim.person.support.KjerneinfoMapper;
import no.nav.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

@Configuration
@Import({no.nav.kodeverk.consumer.config.ConsumerConfig.class})
public class PersonKjerneinfoMapperConfig {

    @Inject
    private KodeverkmanagerBi kodeverkmanagerBean;

    @Bean
    public KjerneinfoMapper kjerneinfoMapperBean() {
        return new KjerneinfoMapper(kodeverkmanagerBean);
    }
}

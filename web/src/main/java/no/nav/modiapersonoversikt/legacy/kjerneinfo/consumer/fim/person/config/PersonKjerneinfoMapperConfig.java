package no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.config;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.support.KjerneinfoMapper;
import no.nav.modiapersonoversikt.consumer.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@Import({no.nav.modiapersonoversikt.consumer.kodeverk.consumer.config.KodeverkConsumerConfig.class})
public class PersonKjerneinfoMapperConfig {

    @Autowired
    private KodeverkmanagerBi kodeverkmanagerBean;

    @Bean
    public KjerneinfoMapper kjerneinfoMapperBean() {
        return new KjerneinfoMapper(kodeverkmanagerBean);
    }
}

package no.nav.kjerneinfo.consumer.fim.person.config;

import no.nav.kjerneinfo.consumer.fim.person.support.KjerneinfoMapper;
import no.nav.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@Import({no.nav.kodeverk.consumer.config.ConsumerConfig.class})
public class PersonKjerneinfoMapperConfig {

    @Autowired
    private KodeverkmanagerBi kodeverkmanagerBean;

    @Bean
    public KjerneinfoMapper kjerneinfoMapperBean() {
        return new KjerneinfoMapper(kodeverkmanagerBean);
    }
}

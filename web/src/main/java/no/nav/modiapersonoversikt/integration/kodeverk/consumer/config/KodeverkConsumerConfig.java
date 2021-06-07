package no.nav.modiapersonoversikt.integration.kodeverk.consumer.config;

import no.nav.modiapersonoversikt.integration.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi;
import no.nav.modiapersonoversikt.integration.kodeverk.consumer.fim.kodeverk.support.DefaultKodeverkmanager;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@Import(EnvironmentPropertiesConfig.class)
@EnableCaching
public class KodeverkConsumerConfig {

    @Autowired
    private KodeverkPortType kodeverkPortType;

    @Bean
    public KodeverkmanagerBi kodeverkmanagerBean() {
        return new DefaultKodeverkmanager(kodeverkPortType);
    }
}

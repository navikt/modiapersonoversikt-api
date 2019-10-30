package no.nav.kodeverk.consumer.config;

import no.nav.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi;
import no.nav.kodeverk.consumer.fim.kodeverk.support.DefaultKodeverkmanager;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

@Configuration
@Import(EnvironmentPropertiesConfig.class)
@EnableCaching
public class ConsumerConfig {

    @Inject
    private KodeverkPortType kodeverkPortType;

    @Bean(name = "kodeverkmanagerBean")
    public KodeverkmanagerBi kodeverkmanagerBean() {
        return new DefaultKodeverkmanager(kodeverkPortType);
    }
}

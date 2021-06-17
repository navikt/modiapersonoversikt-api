package no.nav.modiapersonoversikt.consumer.kodeverk2.config;


import no.nav.modiapersonoversikt.consumer.kodeverk2.JsonKodeverk;
import no.nav.modiapersonoversikt.consumer.kodeverk2.Kodeverk;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KodeverkConfig {

    @Bean
    public Kodeverk kodeverk() {
        return new JsonKodeverk(getClass().getResourceAsStream("/kodeverk.json"));
    }
}
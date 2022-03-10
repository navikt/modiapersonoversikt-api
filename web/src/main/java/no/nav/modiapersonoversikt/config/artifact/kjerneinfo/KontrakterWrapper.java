package no.nav.modiapersonoversikt.config.artifact.kjerneinfo;

import no.nav.modiapersonoversikt.consumer.arena.kontrakter.consumer.fim.config.OppfolgingskontraktConsumerConfig;
import no.nav.modiapersonoversikt.consumer.arena.kontrakter.consumer.fim.config.YtelseskontraktConsumerConfig;
import no.nav.modiapersonoversikt.consumer.arena.kontrakter.consumer.fim.oppfolgingskontrakt.support.DefaultOppfolgingskontraktService;
import no.nav.modiapersonoversikt.consumer.arena.kontrakter.consumer.fim.ytelseskontrakt.support.DefaultYtelseskontraktService;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.OppfoelgingPortType;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.YtelseskontraktV3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import org.springframework.beans.factory.annotation.Autowired;

import static no.nav.modiapersonoversikt.config.artifact.kjerneinfo.KontrakterConsumerConfigImpl.createOppfolgingskontraktService;
import static no.nav.modiapersonoversikt.config.artifact.kjerneinfo.KontrakterConsumerConfigImpl.createYtelseskontraktService;

@Configuration
@Import({
        OppfolgingskontraktConsumerConfig.class,
        YtelseskontraktConsumerConfig.class})
public class KontrakterWrapper {

    @Autowired
    private YtelseskontraktV3 ytelseskontraktPortType;


    @Autowired
    private OppfoelgingPortType oppfoelgingPortType;

    @Bean
    public DefaultYtelseskontraktService ytelseskontraktService() {
        return createYtelseskontraktService(ytelseskontraktPortType);
    }

    @Bean
    public DefaultOppfolgingskontraktService oppfolgingskontraktService() {
        return createOppfolgingskontraktService(oppfoelgingPortType);
    }
}

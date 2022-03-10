package no.nav.modiapersonoversikt.consumer.arena.kontrakter.consumer.config;

import no.nav.modiapersonoversikt.consumer.arena.kontrakter.consumer.fim.ytelseskontrakt.YtelseskontraktServiceBi;
import no.nav.modiapersonoversikt.consumer.arena.kontrakter.consumer.fim.config.OppfolgingskontraktConsumerConfig;
import no.nav.modiapersonoversikt.consumer.arena.kontrakter.consumer.fim.config.YtelseskontraktConsumerConfig;
import no.nav.modiapersonoversikt.consumer.arena.kontrakter.consumer.fim.mapping.YtelseskontraktMapper;
import no.nav.modiapersonoversikt.consumer.arena.kontrakter.consumer.fim.oppfolgingskontrakt.OppfolgingskontraktServiceBi;
import no.nav.modiapersonoversikt.consumer.arena.kontrakter.consumer.fim.oppfolgingskontrakt.support.DefaultOppfolgingskontraktService;
import no.nav.modiapersonoversikt.consumer.arena.kontrakter.consumer.fim.ytelseskontrakt.support.DefaultYtelseskontraktService;
import no.nav.modiapersonoversikt.consumer.arena.kontrakter.consumer.utils.OppfolgingskontraktMapper;
import no.nav.tjeneste.virksomhet.oppfoelging.v1.OppfoelgingPortType;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.YtelseskontraktV3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@Import({OppfolgingskontraktConsumerConfig.class, YtelseskontraktConsumerConfig.class})
public class KontrakterConsumerConfig {

	@Autowired
	private YtelseskontraktV3 ytelseskontraktPortType;

	@Autowired
	private OppfoelgingPortType oppfoelgingPortType;

	@Bean
	public YtelseskontraktServiceBi ytelseskontraktServiceBi() {
		DefaultYtelseskontraktService ytelseskontraktService = new DefaultYtelseskontraktService();
		ytelseskontraktService.setYtelseskontraktService(ytelseskontraktPortType);
		ytelseskontraktService.setMapper(YtelseskontraktMapper.getInstance());
		return ytelseskontraktService;
	}

	@Bean
	public OppfolgingskontraktServiceBi oppfolgingskontraktServiceBi() {
		DefaultOppfolgingskontraktService oppfolgingskontraktServiceBi = new DefaultOppfolgingskontraktService();
		oppfolgingskontraktServiceBi.setOppfolgingskontraktService(oppfoelgingPortType);
		oppfolgingskontraktServiceBi.setMapper(OppfolgingskontraktMapper.getInstance());
		return oppfolgingskontraktServiceBi;
	}
}

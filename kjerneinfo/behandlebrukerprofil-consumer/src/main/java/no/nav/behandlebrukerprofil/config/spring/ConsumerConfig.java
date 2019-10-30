package no.nav.behandlebrukerprofil.config.spring;

import no.nav.behandlebrukerprofil.consumer.BehandleBrukerprofilServiceBi;
import no.nav.behandlebrukerprofil.consumer.support.DefaultBehandleBrukerprofilService;
import no.nav.behandlebrukerprofil.consumer.support.mapping.BehandleBrukerprofilMapper;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.BehandleBrukerprofilV2;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

@Configuration
@Import({BehandleBrukerprofilConsumerConfig.class})
public class ConsumerConfig {

	@Inject
	private BehandleBrukerprofilV2 behandleBrukerprofilPortType;
	@Inject
	private BehandleBrukerprofilV2 selfTestBehandleBrukerprofilPortType;
	@Inject
	private CacheManager cacheManager;

	@Bean
	public BehandleBrukerprofilServiceBi behandleBrukerprofilServiceBi() {
		return new DefaultBehandleBrukerprofilService(
				behandleBrukerprofilPortType,
				selfTestBehandleBrukerprofilPortType,
				BehandleBrukerprofilMapper.getInstance(),
				cacheManager
		);
	}
}

package no.nav.kodeverk.consumer.fim.kodeverk.config;

import no.nav.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi;
import no.nav.kodeverk.consumer.fim.kodeverk.support.DefaultKodeverkmanager;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class KodeverkSpringTestConfig {

	@Bean(name="kodeverkmanagerBean")
	public KodeverkmanagerBi kodeverkmanagerBean() {
		return new DefaultKodeverkmanager(mock(KodeverkPortType.class));
	}
}

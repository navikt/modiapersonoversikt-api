package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.henvendelse;

import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class HenvendelseEndpointCacheTestConfig {

    @Bean
    public HenvendelsePortType henvendelsePortType() {
        String key = "henvendelse-key";
        System.setProperty(key, "true");
        return mock(HenvendelsePortType.class);
    }
}

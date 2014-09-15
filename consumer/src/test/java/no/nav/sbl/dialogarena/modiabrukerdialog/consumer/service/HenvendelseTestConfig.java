package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class HenvendelseTestConfig {

    @Bean
    public HenvendelsePortType henvendelsePortType() {
        return mock(HenvendelsePortType.class);
    }

    @Bean
    public SendUtHenvendelsePortType sendUtHenvendelsePortType() {
        return mock(SendUtHenvendelsePortType.class);
    }

}

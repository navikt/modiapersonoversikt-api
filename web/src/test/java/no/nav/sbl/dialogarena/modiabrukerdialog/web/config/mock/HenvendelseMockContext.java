package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.sendhenvendelse.SendHenvendelsePortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class HenvendelseMockContext {

    @Bean(name = "ws")
    public SendHenvendelsePortType ws() {
        return mock(SendHenvendelsePortType.class);
    }
}

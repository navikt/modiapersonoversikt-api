package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class SakOgBehandlingPortTypeMock {

    @Bean
    public SakOgBehandlingPortType sakOgBehandlingPortType() {
        return mock(SakOgBehandlingPortType.class);
    }

}

package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.tildeloppgave.v1.TildelOppgaveV1;
import no.nav.tjeneste.virksomhet.tildeloppgave.v1.WSTildelFlereOppgaverRequest;
import no.nav.tjeneste.virksomhet.tildeloppgave.v1.WSTildelFlereOppgaverResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GsakTildelOppgaveV1PortTypeMock {

    @Bean
    public static TildelOppgaveV1 createTildelOppgavePortTypeMock() {
        return new TildelOppgaveV1() {
            @Override
            public void ping() {}

            @Override
            public WSTildelFlereOppgaverResponse tildelFlereOppgaver(WSTildelFlereOppgaverRequest wsTildelFlereOppgaverRequest) {
                return new WSTildelFlereOppgaverResponse();
            }
        };
    }

}

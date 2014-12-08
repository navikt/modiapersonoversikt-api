package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class OppgaveTestConfig {

    @Bean
    public OppgaveV3 oppgave() {
        return mock(OppgaveV3.class);
    }

    @Bean
    public OppgavebehandlingV3 oppgavebehandling() {
        return mock(OppgavebehandlingV3.class);
    }

}

package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services;

import no.nav.virksomhet.tjenester.oppgave.v2.binding.Oppgave;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.binding.Oppgavebehandling;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class OppgaveTestConfig {

    @Bean
    public no.nav.virksomhet.tjenester.oppgave.v2.binding.Oppgave oppgave(){
        return mock(Oppgave.class);
    }

    @Bean
    public Oppgavebehandling oppgavebehandling(){
        return mock(Oppgavebehandling.class);
    }

}

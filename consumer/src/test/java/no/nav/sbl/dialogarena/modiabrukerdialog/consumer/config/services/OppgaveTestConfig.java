package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services;

import no.nav.virksomhet.tjenester.oppgave.v2.Oppgave;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class OppgaveTestConfig {

    @Bean
    public Oppgave oppgave(){
        return mock(Oppgave.class);
    }

}

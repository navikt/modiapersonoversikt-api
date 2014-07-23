package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services;

import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.Oppgavebehandling;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class OppgaveTestConfig {

    @Bean
    public OppgaveV3 oppgave(){
        return mock(OppgaveV3.class);
    }

    @Bean
    public Oppgavebehandling oppgavebehandling(){
        return mock(Oppgavebehandling.class);
    }

}

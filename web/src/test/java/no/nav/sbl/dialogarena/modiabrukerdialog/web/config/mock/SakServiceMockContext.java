package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.SakService;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.Oppgavebehandling;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class SakServiceMockContext {

    @Bean
    public Oppgavebehandling oppgavebehandling() {
        return mock(Oppgavebehandling.class);
    }

    @Bean
    public HenvendelsePortType henvendelsePortType(){
        return mock(HenvendelsePortType.class);
    }

    @Bean
    public SendUtHenvendelsePortType sendUtHenvendelsePortType(){
        return mock(SendUtHenvendelsePortType.class);
    }

    @Bean
    public OppgaveV3 oppgave() {
        return mock(OppgaveV3.class);
    }

    @Bean
    public SakService sakService() {
        return mock(SakService.class);
    }
}

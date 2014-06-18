package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Sporsmaal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.SakService;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.sendhenvendelse.SendHenvendelsePortType;
import no.nav.virksomhet.tjenester.oppgave.v2.Oppgave;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.Oppgavebehandling;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    public SendHenvendelsePortType sendHenvendelsePortType(){
        return mock(SendHenvendelsePortType.class);
    }

    @Bean
    public Oppgave oppgave() {
        return mock(Oppgave.class);
    }

    @Bean
    public SakService sakService() {
        SakService mock = mock(SakService.class);
        when(mock.getSporsmaal(anyString())).thenReturn(new Sporsmaal("", DateTime.now()));
        return mock;
    }
}

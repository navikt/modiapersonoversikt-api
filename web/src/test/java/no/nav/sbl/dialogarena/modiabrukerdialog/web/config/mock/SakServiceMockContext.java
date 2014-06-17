package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.SakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import no.nav.virksomhet.tjenester.oppgave.v2.Oppgave;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.Oppgavebehandling;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Meldingstype.SPORSMAL;
import static org.joda.time.DateTime.now;
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
    public Oppgave oppgave() {
        return mock(Oppgave.class);
    }

    @Bean
    public SakService sakService() {
        SakService mock = mock(SakService.class);
        when(mock.getSakFromHenvendelse(anyString())).thenReturn(new Melding("", SPORSMAL, now()));
        return mock;
    }
}

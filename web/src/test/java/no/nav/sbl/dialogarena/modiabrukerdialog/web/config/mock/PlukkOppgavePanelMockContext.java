package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.PlukkOppgaveService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.mockito.Mockito.mock;

@Configuration
@Import(PersonPageMockContext.class)
public class PlukkOppgavePanelMockContext {
    @Bean
    public PlukkOppgaveService plukkOppgaveService() {
        return mock(PlukkOppgaveService.class);
    }
}

package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.sbl.dialogarena.varsel.service.VarslerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class VarslingMockContext {

    @Bean
    public VarslerService varslerService() {
        return mock(VarslerService.class);
    }
}

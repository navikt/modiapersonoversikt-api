package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.sykmeldingsperioder.widget.SykepengerWidgetService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class SykepengerWidgetMockContext {

    @Bean
    public SykepengerWidgetService sykepengerWidgetService() {
        return mock(SykepengerWidgetService.class);
    }

}

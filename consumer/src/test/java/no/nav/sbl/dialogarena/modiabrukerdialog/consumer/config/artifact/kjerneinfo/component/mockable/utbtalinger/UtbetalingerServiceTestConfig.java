package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.utbtalinger;

import no.nav.sykmeldingsperioder.consumer.utbetalinger.UtbetalingerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class UtbetalingerServiceTestConfig {

    @Bean
    public UtbetalingerService utbetalingerService() {
        return mock(UtbetalingerService.class);
    }

}

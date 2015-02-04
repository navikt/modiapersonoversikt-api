package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.utbetaling;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import no.nav.tjeneste.virksomhet.utbetaling.v1.UtbetalingV1;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class UtbetalingWrapperTestConfig {

    @Bean
    @Qualifier("utbetalingPortTypeWrapper")
    public Wrapper<UtbetalingV1> utbetalingPortTypeWrapper() {
        return new Wrapper<>(mock(UtbetalingV1.class));
    }

    @Bean
    @Qualifier("utbetalingPortTypeWrapperMock")
    public Wrapper<UtbetalingV1> utbetalingPortTypeWrapperMock() {
        return new Wrapper<>(mock(UtbetalingV1.class));
    }
}

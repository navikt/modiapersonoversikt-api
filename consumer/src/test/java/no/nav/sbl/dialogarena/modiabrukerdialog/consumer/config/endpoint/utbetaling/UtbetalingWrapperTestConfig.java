package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.utbetaling;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import no.nav.virksomhet.tjenester.utbetaling.v2.UtbetalingPortType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class UtbetalingWrapperTestConfig {

    @Bean
    @Qualifier("utbetalingPortTypeWrapper")
    public Wrapper<UtbetalingPortType> utbetalingPortTypeWrapper() {
        return new Wrapper<>(mock(UtbetalingPortType.class));
    }

    @Bean
    @Qualifier("utbetalingPortTypeWrapperMock")
    public Wrapper<UtbetalingPortType> utbetalingPortTypeWrapperMock() {
        return new Wrapper<>(mock(UtbetalingPortType.class));
    }
}

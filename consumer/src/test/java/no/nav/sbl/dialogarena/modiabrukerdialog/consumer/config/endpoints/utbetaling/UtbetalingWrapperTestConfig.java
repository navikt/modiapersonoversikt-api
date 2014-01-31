package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.utbetaling;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.UtbetalingPortTypeWrapper;
import no.nav.virksomhet.tjenester.utbetaling.v2.UtbetalingPortType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class UtbetalingWrapperTestConfig {

    @Bean
    @Qualifier("utbetalingPortTypeWrapper")
    public UtbetalingPortTypeWrapper utbetalingPortTypeWrapper() {
        final UtbetalingPortTypeWrapper mock = mock(UtbetalingPortTypeWrapper.class);
        final UtbetalingPortType utbetalingPortType = mock(UtbetalingPortType.class);
        when(mock.getPortType()).thenReturn(utbetalingPortType);
        return mock;
    }

    @Bean
    @Qualifier("utbetalingPortTypeWrapperMock")
    public UtbetalingPortTypeWrapper utbetalingPortTypeWrapperMock() {
        final UtbetalingPortTypeWrapper mock = mock(UtbetalingPortTypeWrapper.class);
        final UtbetalingPortType utbetalingPortType = mock(UtbetalingPortType.class);
        when(mock.getPortType()).thenReturn(utbetalingPortType);
        return mock;
    }
}

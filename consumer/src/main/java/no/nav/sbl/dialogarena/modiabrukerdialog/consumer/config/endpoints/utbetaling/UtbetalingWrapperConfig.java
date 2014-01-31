package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.utbetaling;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.porttypeimpl.UtbetalingPortTypeImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.UtbetalingPortTypeWrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.UtbetalingPortTypeMock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;

@Configuration
public class UtbetalingWrapperConfig {

    @Value("${utbetalingendpoint.v2.url}")
    private URL endpoint;

    @Bean
    @Qualifier("utbetalingPortTypeWrapper")
    public UtbetalingPortTypeWrapper utbetalingPortTypeWrapper() {
        return new UtbetalingPortTypeWrapper(new UtbetalingPortTypeImpl(endpoint).utbetalingPortType());
    }

    @Bean
    @Qualifier("utbetalingPortTypeWrapperMock")
    public UtbetalingPortTypeWrapper utbetalingPortTypeWrapperMock() {
        return new UtbetalingPortTypeWrapper(new UtbetalingPortTypeMock().utbetalingPortType());
    }
}

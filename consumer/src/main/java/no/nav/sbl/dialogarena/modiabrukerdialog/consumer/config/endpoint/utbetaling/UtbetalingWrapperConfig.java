package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.utbetaling;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.UtbetalingPortTypeMock;
import no.nav.tjeneste.virksomhet.utbetaling.v1.UtbetalingV1;
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
    public Wrapper<UtbetalingV1> utbetalingPortTypeWrapper() {
        return new Wrapper<>(new UtbetalingPortTypeImpl(endpoint).utbetalingPortType());
    }

    @Bean
    @Qualifier("utbetalingPortTypeWrapperMock")
    public Wrapper<UtbetalingV1> utbetalingPortTypeWrapperMock() {
        return new Wrapper<>(new UtbetalingPortTypeMock().utbetalingPortType());
    }
}

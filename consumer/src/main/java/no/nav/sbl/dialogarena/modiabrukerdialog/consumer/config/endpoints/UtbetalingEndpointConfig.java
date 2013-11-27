package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import no.nav.modig.modia.ping.Pingable;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.MockPingable;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.UtbetalingPortTypeMock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtbetalingEndpointConfig {

    public static final String UTBETALING_KEY = "start.utbetaling.withmock";
    // private  UtbetalingPortType portType = new UtbetalingPortTypeImpl().utbetalingPortType();
    private no.nav.virksomhet.tjenester.utbetaling.v2.Utbetaling portTypeMock = new UtbetalingPortTypeMock().utbetalingPortType();

    @Bean
    public no.nav.virksomhet.tjenester.utbetaling.v2.Utbetaling utbetalingPortType() {
        // return createSwitcher(portType, portTypeMock, UTBETALING_KEY, UtbetalingPortType.class);
        return portTypeMock;
    }

    @Bean
    public Pingable utbetalingPing() {
        return new MockPingable("UtbetalingEndpoint");
    }

}

package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import no.nav.modig.modia.ping.Pingable;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.porttypeimpl.UtbetalingPortTypeImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.MockPingable;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.UtbetalingPortTypeMock;
import no.nav.virksomhet.tjenester.utbetaling.v2.UtbetalingPortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtbetalingEndpointConfig {

    private boolean useMock;
    private final UtbetalingPortTypeImpl portType;
    private final UtbetalingPortTypeMock portTypeMock;

    public UtbetalingEndpointConfig() {
        useMock = true;
        portType = new UtbetalingPortTypeImpl();
        portTypeMock = new UtbetalingPortTypeMock();
    }

    @Bean
    public UtbetalingPortType utbetalingPortType() {
        if(useMock) {
            return portTypeMock.utbetalingPortType();
        }
        return portType.utbetalingPortType();
    }

    @Bean
    public Pingable utbetalingPing() {
        return new MockPingable("UtbetalingEndpoint");
    }
}

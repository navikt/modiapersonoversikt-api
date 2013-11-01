package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import no.nav.modig.modia.ping.Pingable;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.porttypeimpl.UtbetalingPortTypeImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.MockPingable;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.UtbetalingPortTypeMock;
import no.nav.virksomhet.tjenester.utbetaling.v2.UtbetalingPortType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.ConfigUtil.setUseMock;

@Configuration
public class UtbetalingEndpointConfig {
    private static final Logger LOG = LoggerFactory.getLogger(UtbetalingEndpointConfig.class);
    private final UtbetalingPortTypeImpl portType;
    private final UtbetalingPortTypeMock portTypeMock;
    private boolean useMock;

    public UtbetalingEndpointConfig() {
        useMock = setUseMock("start.utbetaling.withintegration", LOG);
        portType = new UtbetalingPortTypeImpl();
        portTypeMock = new UtbetalingPortTypeMock();
    }

    @Bean
    public UtbetalingPortType utbetalingPortType() {
        if (useMock) {
            return portTypeMock.utbetalingPortType();
        }
        return portType.utbetalingPortType();
    }

    @Bean
    public Pingable utbetalingPing() {
        return new MockPingable("UtbetalingEndpoint");
    }
}

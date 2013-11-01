package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import no.nav.modig.modia.ping.Pingable;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.porttypeimpl.UtbetalingPortTypeImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.MockPingable;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.UtbetalingPortTypeMock;
import no.nav.virksomhet.tjenester.utbetaling.v2.UtbetalingPortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.InstanceSwitcher.createSwitcher;

@Configuration
public class UtbetalingEndpointConfig {
    private  UtbetalingPortType portType = new UtbetalingPortTypeImpl().utbetalingPortType();
    private  UtbetalingPortType portTypeMock = new UtbetalingPortTypeMock().utbetalingPortType();
    private  String key = "start.utbetaling.withintegration";


    @Bean
    public UtbetalingPortType utbetalingPortType() {
        return createSwitcher(portType, portTypeMock, key, UtbetalingPortType.class);
    }

    @Bean
    public Pingable utbetalingPing() {
        return new MockPingable("UtbetalingEndpoint");
    }

}

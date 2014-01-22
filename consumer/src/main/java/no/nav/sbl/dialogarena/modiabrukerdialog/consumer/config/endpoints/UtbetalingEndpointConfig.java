package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import no.nav.modig.modia.ping.Pingable;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.porttypeimpl.UtbetalingPortTypeImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.MockPingable;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.UtbetalingPortTypeMock;
import no.nav.virksomhet.tjenester.utbetaling.v2.UtbetalingPortType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.InstanceSwitcher.createSwitcher;

@Configuration
public class UtbetalingEndpointConfig {

    @Value("${utbetalingendpoint.v2.url}")
    private URL endpoint;

    public static final String UTBETALING_KEY = "start.utbetaling.withmock";


    @Bean
    public UtbetalingPortType utbetalingPortType() {
        UtbetalingPortType portType = new UtbetalingPortTypeImpl(endpoint).utbetalingPortType();
        UtbetalingPortType portTypeMock = new UtbetalingPortTypeMock().utbetalingPortType();
        return createSwitcher(portType, portTypeMock, UTBETALING_KEY, UtbetalingPortType.class);
    }

    @Bean
    public Pingable utbetalingPing() {
        return new MockPingable("UtbetalingEndpoint");
    }

}

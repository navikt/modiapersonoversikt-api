package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * MODIA ønsker å selv definere opp endpoints, slik at kontekst for samme endepunkt
 * enkelt kan gjenbrukes på tvers av komponenter
 */
@Configuration
@Import({
        AktorEndpointConfig.class,
        BesvareHenvendelseEndpointConfig.class,
        HenvendelseMeldingerEndpointConfig.class,
        OppgavebehandlingEndpointConfig.class,
        SakOgBehandlingEndpointConfig.class,
        UtbetalingEndpointConfig.class
})
public class EndpointsConfig {

    public static final int MODIA_RECEIVE_TIMEOUT = 4000;
    public static final int MODIA_CONNECTION_TIMEOUT = 4000;
}

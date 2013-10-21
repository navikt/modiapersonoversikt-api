package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.endpoints;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * MODIA ønsker å selv definere opp endpoints, slik at kontekst for samme endepunkt
 * enkelt kan gjenbrukes på tvers av komponenter
 */
@Configuration
@Import({
        BesvareHenvendelseEndpointConfig.Default.class,
        HenvendelseEndpointConfig.Test.class,
        OppgavebehandlingEndpointConfig.Default.class,
        SakOgBehandlingEndpointConfig.Default.class
})
public class EndpointsConfig {
}

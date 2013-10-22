package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.endpoints;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * MODIA ønsker å selv definere opp endpoints, slik at kontekst for samme endepunkt
 * enkelt kan gjenbrukes på tvers av komponenter
 */
@Configuration
@Import({
        BesvareHenvendelseEndpointConfig.class,
        HenvendelseEndpointConfig.class,
        OppgavebehandlingEndpointConfig.class,
        SakOgBehandlingEndpointConfig.class
})
public class EndpointsConfig {
}

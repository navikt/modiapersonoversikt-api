package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.tjenester;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * MODIA ønsker å selv definere opp tjenestene, slik at kontekst for samme endepunkt
 * enkelt kan gjenbrukes på tvers av komponenter
 */
@Configuration
@Import({
        BesvareHenvendelseTjenesteConfig.Default.class,
        HenvendelseTjenesteConfig.Default.class,
        OppgavebehandlingTjenesteConfig.Default.class,
        SakOgBehandlingTjenesteConfig.Default.class
})
public class TjenesterConfig {
}

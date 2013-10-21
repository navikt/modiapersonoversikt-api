package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.services;

import no.nav.sbl.dialogarena.soknader.liste.config.SoknaderConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * MODIA ønsker å selv definere opp sine tjenester for å full kontroll over springoppsettet
 * til sine komponenter
 */
@Configuration
@Import({SoknaderConfig.class})
public class ServicesConfig {
}

package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services;

import no.nav.sbl.dialogarena.utbetaling.lamell.context.UbetalingLamellContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * MODIA ønsker å selv wire inn sine komponenters tjenester for å full kontroll over springoppsettet.
 */
@Configuration
@Import({UbetalingLamellContext.class})
public class ServicesConfig {

}

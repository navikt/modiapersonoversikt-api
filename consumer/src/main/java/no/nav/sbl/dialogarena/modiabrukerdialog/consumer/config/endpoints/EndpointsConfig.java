package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.kodeverk.KodeverkV2EndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.kodeverk.KodeverkV2WrapperConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.utbetaling.UtbetalingEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.utbetaling.UtbetalingWrapperConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v2.gsak.GsakOppgaveV2EndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v2.gsak.GsakOppgavebehandlingV2EndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v2.henvendelseaktivitet.HenvendelseAktivitetV2EndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v2.henvendelseinformasjon.HenvendelseEndpointConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * MODIA ønsker å selv definere opp endpoints, slik at kontekst for samme endepunkt
 * enkelt kan gjenbrukes på tvers av komponenter
 */
@Configuration
@Import({
        UtbetalingWrapperConfig.class,
        UtbetalingEndpointConfig.class,
        KodeverkV2EndpointConfig.class,
        KodeverkV2WrapperConfig.class,
        HenvendelseAktivitetV2EndpointConfig.class,
        HenvendelseEndpointConfig.class,
        GsakOppgaveV2EndpointConfig.class,
        GsakOppgavebehandlingV2EndpointConfig.class
})
public class EndpointsConfig {

    public static final int MODIA_RECEIVE_TIMEOUT = 4000;
    public static final int MODIA_CONNECTION_TIMEOUT = 4000;

}

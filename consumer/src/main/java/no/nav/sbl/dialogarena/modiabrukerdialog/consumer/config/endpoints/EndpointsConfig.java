package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.aktoer.AktoerEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.henvendelsesoknader.HenvendelseSoknaderEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.kodeverk.KodeverkV2EndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.kodeverk.KodeverkV2WrapperConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.sakogbehandling.SakOgBehandlingEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.utbetaling.UtbetalingEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.utbetaling.UtbetalingWrapperConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v1.gosys.GosysAnsattEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v1.gsak.hentsaker.GsakHentSakslisteEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v2.henvendelse.BehandleHenvendelseEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v2.henvendelse.HenvendelseEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v2.henvendelse.SendUtHenvendelseEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v2.journalforing.BehandleJournalV2EndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v3.gsak.GsakOppgaveV3EndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v3.gsak.GsakOppgavebehandlingV3EndpointConfig;
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
        SendUtHenvendelseEndpointConfig.class,
        BehandleHenvendelseEndpointConfig.class,
        HenvendelseEndpointConfig.class,
        GsakOppgaveV3EndpointConfig.class,
        GsakOppgavebehandlingV3EndpointConfig.class,
        BehandleJournalV2EndpointConfig.class,
        GsakHentSakslisteEndpointConfig.class,
        SakOgBehandlingEndpointConfig.class,
        AktoerEndpointConfig.class,
        HenvendelseSoknaderEndpointConfig.class,
        GosysAnsattEndpointConfig.class
})
public class EndpointsConfig {

    public static final int MODIA_RECEIVE_TIMEOUT = 4000;
    public static final int MODIA_CONNECTION_TIMEOUT = 4000;

}

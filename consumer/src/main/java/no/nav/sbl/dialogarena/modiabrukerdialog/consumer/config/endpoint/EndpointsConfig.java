package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.aktor.AktorEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.cms.CmsEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.henvendelsesoknader.HenvendelseSoknaderEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.kodeverk.KodeverkV2EndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.kodeverk.KodeverkV2WrapperConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.sakogbehandling.SakOgBehandlingEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.utbetaling.UtbetalingEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.utbetaling.UtbetalingWrapperConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.gsak.hentsaker.GsakHentSakslisteEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg.NAVAnsattEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg.NAVOrgEnhetEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.henvendelse.BehandleHenvendelseEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.henvendelse.HenvendelseEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.henvendelse.SendUtHenvendelseEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.journalforing.BehandleJournalV2EndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v3.gsak.GsakOppgaveV3EndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v3.gsak.GsakOppgavebehandlingV3EndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.henvendelse.ArbeidOgAktivitetEndpointConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * MODIA ønsker å selv definere opp endpoints, slik at config for samme endepunkt
 * kan gjenbrukes på tvers av komponenter
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
        ArbeidOgAktivitetEndpointConfig.class,
        GsakOppgavebehandlingV3EndpointConfig.class,
        BehandleJournalV2EndpointConfig.class,

        GsakHentSakslisteEndpointConfig.class,
        SakOgBehandlingEndpointConfig.class,
        AktorEndpointConfig.class,
        HenvendelseSoknaderEndpointConfig.class,
        NAVAnsattEndpointConfig.class,

        NAVOrgEnhetEndpointConfig.class,
        CmsEndpointConfig.class
})
public class EndpointsConfig {

    public static final int MODIA_RECEIVE_TIMEOUT = 4000;
    public static final int MODIA_CONNECTION_TIMEOUT = 4000;

}

package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.cms.CmsEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.cms.CmsSkrivestotteConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.joark.JoarkEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.kodeverksmapper.KodeverksmapperEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.oppfolgingsinfo.OppfolgingsinfoEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.unleash.UnleashEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.aktor.AktorEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.arbeidsfordeling.ArbeidsfordelingV1EndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.arena.arbeidogaktivitet.ArbeidOgAktivitetEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.egenansatt.EgenAnsattV1EndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.gsak.behandlesak.GsakOpprettSakEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.gsak.hentsaker.GsakSakV1EndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.gsak.GsakTildelOppgaveV1EndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.henvendelsesoknader.HenvendelseSoknaderEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg.NAVAnsattEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.norg.NAVOrgEnhetEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.pensjonsak.PensjonSakEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.sakogbehandling.SakOgBehandlingEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.utbetaling.UtbetalingEndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.henvendelse.*;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.kodeverk.KodeverkV2EndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.organisasjonenhet.OrganisasjonEnhetV2EndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.organisasjonenhetkontaktinformasjon.OrganisasjonEnhetKontaktinformasjonV1EndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v3.gsak.GsakOppgaveV3EndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v3.gsak.GsakOppgavebehandlingV3EndpointConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * MODIA ønsker å selv definere opp endpoints, slik at config for samme endepunkt
 * kan gjenbrukes på tvers av komponenter
 */
@Configuration
@Import({
        UtbetalingEndpointConfig.class,
        KodeverkV2EndpointConfig.class,
        SendUtHenvendelseEndpointConfig.class,
        BehandleHenvendelseEndpointConfig.class,
        HenvendelseEndpointConfig.class,
        JoarkEndpointConfig.class,
        GsakOppgaveV3EndpointConfig.class,
        GsakOppgavebehandlingV3EndpointConfig.class,
        GsakTildelOppgaveV1EndpointConfig.class,
        GsakSakV1EndpointConfig.class,
        GsakOpprettSakEndpointConfig.class,
        SakOgBehandlingEndpointConfig.class,
        AktorEndpointConfig.class,
        HenvendelseSoknaderEndpointConfig.class,
        NAVAnsattEndpointConfig.class,
        NAVOrgEnhetEndpointConfig.class,
        CmsEndpointConfig.class,
        CmsSkrivestotteConfig.class,
        ArbeidOgAktivitetEndpointConfig.class,
        PensjonSakEndpointConfig.class,
        VarslingEndpointConfig.class,
        OrganisasjonEnhetV2EndpointConfig.class,
        OrganisasjonEnhetKontaktinformasjonV1EndpointConfig.class,
        OppfolgingsinfoEndpointConfig.class,
        EgenAnsattV1EndpointConfig.class,
        ArbeidsfordelingV1EndpointConfig.class,
        KodeverksmapperEndpointConfig.class,
        UnleashEndpointConfig.class
})
public class EndpointsConfig {

}

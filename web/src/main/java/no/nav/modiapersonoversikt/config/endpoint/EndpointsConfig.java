package no.nav.modiapersonoversikt.config.endpoint;

import no.nav.modiapersonoversikt.config.endpoint.cms.CmsEndpointConfig;
import no.nav.modiapersonoversikt.config.endpoint.joark.InnsynJournalEndpointConfig;
import no.nav.modiapersonoversikt.config.endpoint.joark.JoarkEndpointConfig;
import no.nav.modiapersonoversikt.config.endpoint.kodeverksmapper.KodeverksmapperEndpointConfig;
import no.nav.modiapersonoversikt.config.endpoint.oppfolgingsinfo.OppfolgingsinfoApiEndpointConfig;
import no.nav.modiapersonoversikt.config.endpoint.v1.arena.arbeidogaktivitet.ArbeidOgAktivitetEndpointConfig;
import no.nav.modiapersonoversikt.config.endpoint.v1.egenansatt.EgenAnsattV1EndpointConfig;
import no.nav.modiapersonoversikt.config.endpoint.v1.henvendelsesoknader.HenvendelseSoknaderEndpointConfig;
import no.nav.modiapersonoversikt.config.endpoint.v1.norg.NAVAnsattEndpointConfig;
import no.nav.modiapersonoversikt.config.endpoint.v1.norg.NAVOrgEnhetEndpointConfig;
import no.nav.modiapersonoversikt.config.endpoint.v1.pensjonsak.PensjonSakEndpointConfig;
import no.nav.modiapersonoversikt.config.endpoint.v1.personsok.PersonsokEndpointConfig;
import no.nav.modiapersonoversikt.config.endpoint.v1.sakogbehandling.SakOgBehandlingEndpointConfig;
import no.nav.modiapersonoversikt.config.endpoint.v1.utbetaling.UtbetalingEndpointConfig;
import no.nav.modiapersonoversikt.config.endpoint.v2.aktor.AktorV2EndpointConfig;
import no.nav.modiapersonoversikt.config.endpoint.v2.henvendelse.BehandleHenvendelseEndpointConfig;
import no.nav.modiapersonoversikt.config.endpoint.v2.henvendelse.HenvendelseEndpointConfig;
import no.nav.modiapersonoversikt.config.endpoint.v2.henvendelse.SendUtHenvendelseEndpointConfig;
import no.nav.modiapersonoversikt.config.endpoint.v2.kodeverk.KodeverkV2EndpointConfig;
import no.nav.modiapersonoversikt.config.endpoint.v2.organisasjonenhet.OrganisasjonEnhetV2EndpointConfig;
import no.nav.modiapersonoversikt.config.endpoint.v2.organisasjonenhetkontaktinformasjon.OrganisasjonEnhetKontaktinformasjonV1EndpointConfig;
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
        SakOgBehandlingEndpointConfig.class,
        AktorV2EndpointConfig.class,
        HenvendelseSoknaderEndpointConfig.class,
        NAVAnsattEndpointConfig.class,
        NAVOrgEnhetEndpointConfig.class,
        CmsEndpointConfig.class,
        ArbeidOgAktivitetEndpointConfig.class,
        PensjonSakEndpointConfig.class,
        VarslingEndpointConfig.class,
        OrganisasjonEnhetV2EndpointConfig.class,
        OrganisasjonEnhetKontaktinformasjonV1EndpointConfig.class,
        EgenAnsattV1EndpointConfig.class,
        KodeverksmapperEndpointConfig.class,
        UnleashEndpointConfig.class,
        InnsynJournalEndpointConfig.class,
        OppfolgingsinfoApiEndpointConfig.class,
        SfHenvendelseConfig.class,
        PersonsokEndpointConfig.class
})
public class EndpointsConfig {

}

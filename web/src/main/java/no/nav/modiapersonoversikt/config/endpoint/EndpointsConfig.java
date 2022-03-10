package no.nav.modiapersonoversikt.config.endpoint;

import no.nav.modiapersonoversikt.config.endpoint.joark.InnsynJournalEndpointConfig;
import no.nav.modiapersonoversikt.config.endpoint.joark.JoarkEndpointConfig;
import no.nav.modiapersonoversikt.config.endpoint.oppfolgingsinfo.OppfolgingsinfoApiEndpointConfig;
import no.nav.modiapersonoversikt.config.endpoint.v1.arena.arbeidogaktivitet.ArbeidOgAktivitetEndpointConfig;
import no.nav.modiapersonoversikt.config.endpoint.v1.egenansatt.EgenAnsattV1EndpointConfig;
import no.nav.modiapersonoversikt.config.endpoint.v1.henvendelsesoknader.HenvendelseSoknaderEndpointConfig;
import no.nav.modiapersonoversikt.config.endpoint.v1.pensjonsak.PensjonSakEndpointConfig;
import no.nav.modiapersonoversikt.config.endpoint.v1.personsok.PersonsokEndpointConfig;
import no.nav.modiapersonoversikt.config.endpoint.v1.sakogbehandling.SakOgBehandlingEndpointConfig;
import no.nav.modiapersonoversikt.config.endpoint.v1.utbetaling.UtbetalingEndpointConfig;
import no.nav.modiapersonoversikt.service.unleash.UnleashEndpointConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * MODIA ønsker å selv definere opp endpoints, slik at config for samme endepunkt
 * kan gjenbrukes på tvers av komponenter
 */
@Configuration
@Import({
        UtbetalingEndpointConfig.class,
        JoarkEndpointConfig.class,
        SakOgBehandlingEndpointConfig.class,
        HenvendelseSoknaderEndpointConfig.class,
        ArbeidOgAktivitetEndpointConfig.class,
        PensjonSakEndpointConfig.class,
        VarslingEndpointConfig.class,
        EgenAnsattV1EndpointConfig.class,
        UnleashEndpointConfig.class,
        InnsynJournalEndpointConfig.class,
        OppfolgingsinfoApiEndpointConfig.class,
        PersonsokEndpointConfig.class
})
public class EndpointsConfig {

}

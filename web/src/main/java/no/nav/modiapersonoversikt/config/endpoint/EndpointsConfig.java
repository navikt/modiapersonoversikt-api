package no.nav.modiapersonoversikt.config.endpoint;

import no.nav.modiapersonoversikt.consumer.brukervarsel.VarslingEndpointConfig;
import no.nav.modiapersonoversikt.consumer.joark.InnsynJournalEndpointConfig;
import no.nav.modiapersonoversikt.consumer.joark.JoarkEndpointConfig;
import no.nav.modiapersonoversikt.consumer.veilarboppfolging.OppfolgingsinfoApiEndpointConfig;
import no.nav.modiapersonoversikt.consumer.arena.ArbeidOgAktivitetEndpointConfig;
import no.nav.modiapersonoversikt.config.endpoint.v1.egenansatt.EgenAnsattV1EndpointConfig;
import no.nav.modiapersonoversikt.consumer.pesys.PensjonSakEndpointConfig;
import no.nav.modiapersonoversikt.consumer.tps.PersonsokEndpointConfig;
import no.nav.modiapersonoversikt.consumer.sakogbehandling.SakOgBehandlingEndpointConfig;
import no.nav.modiapersonoversikt.consumer.utbetaling.UtbetalingEndpointConfig;
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

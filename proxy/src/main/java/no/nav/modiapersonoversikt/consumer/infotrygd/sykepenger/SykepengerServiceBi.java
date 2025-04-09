package no.nav.modiapersonoversikt.consumer.infotrygd.sykepenger;

import no.nav.modiapersonoversikt.consumer.infotrygd.sykepenger.mapping.to.SykepengerRequest;
import no.nav.modiapersonoversikt.consumer.infotrygd.sykepenger.mapping.to.SykepengerResponse;
import org.springframework.cache.annotation.Cacheable;

/**
 * Interface for tjenesten for sykepenger.
 */
public interface SykepengerServiceBi {

    @Cacheable(value = "sykePengerCache")
    SykepengerResponse hentSykmeldingsperioder(SykepengerRequest request);
}

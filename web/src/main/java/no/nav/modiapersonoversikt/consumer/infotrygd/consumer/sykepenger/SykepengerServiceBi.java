package no.nav.modiapersonoversikt.consumer.infotrygd.consumer.sykepenger;

import no.nav.modiapersonoversikt.consumer.infotrygd.consumer.sykepenger.mapping.to.SykepengerRequest;
import no.nav.modiapersonoversikt.consumer.infotrygd.consumer.sykepenger.mapping.to.SykepengerResponse;
import org.springframework.cache.annotation.Cacheable;

/**
 * Interface for tjenesten for sykepenger.
 */
public interface SykepengerServiceBi {

    @Cacheable(value = "hentSykmeldingsperioderCache")
    SykepengerResponse hentSykmeldingsperioder(SykepengerRequest request);
}

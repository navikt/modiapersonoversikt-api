package no.nav.sykmeldingsperioder.consumer.sykepenger;

import no.nav.sykmeldingsperioder.consumer.sykepenger.mapping.to.SykepengerRequest;
import no.nav.sykmeldingsperioder.consumer.sykepenger.mapping.to.SykepengerResponse;
import org.springframework.cache.annotation.Cacheable;

/**
 * Interface for tjenesten for sykepenger.
 */
public interface SykepengerServiceBi {

    @Cacheable(value = "hentSykmeldingsperioderCache")
    SykepengerResponse hentSykmeldingsperioder(SykepengerRequest request);
}

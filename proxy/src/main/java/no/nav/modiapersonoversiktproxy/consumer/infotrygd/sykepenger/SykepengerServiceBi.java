package no.nav.modiapersonoversiktproxy.consumer.infotrygd.sykepenger;

import no.nav.modiapersonoversiktproxy.consumer.infotrygd.sykepenger.mapping.to.SykepengerRequest;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.sykepenger.mapping.to.SykepengerResponse;
import org.springframework.cache.annotation.Cacheable;

/**
 * Interface for tjenesten for sykepenger.
 */
public interface SykepengerServiceBi {

    @Cacheable(value = "hentSykmeldingsperioderCache")
    SykepengerResponse hentSykmeldingsperioder(SykepengerRequest request);
}

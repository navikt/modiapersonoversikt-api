package no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.foreldrepenger;

import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.foreldrepenger.mapping.to.ForeldrepengerListeRequest;
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.foreldrepenger.mapping.to.ForeldrepengerListeResponse;
import org.springframework.cache.annotation.Cacheable;

/**
 * Interface for tjenesten for foreldrepenger.
 */
public interface ForeldrepengerServiceBi {

    @Cacheable(value = "foreldrePengerCache")
    ForeldrepengerListeResponse hentForeldrepengerListe(ForeldrepengerListeRequest request);
}

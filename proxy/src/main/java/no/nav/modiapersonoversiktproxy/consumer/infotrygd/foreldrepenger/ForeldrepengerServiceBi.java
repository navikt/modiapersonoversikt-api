package no.nav.modiapersonoversiktproxy.consumer.infotrygd.foreldrepenger;

import no.nav.modiapersonoversiktproxy.consumer.infotrygd.foreldrepenger.mapping.to.ForeldrepengerListeRequest;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.foreldrepenger.mapping.to.ForeldrepengerListeResponse;
import org.springframework.cache.annotation.Cacheable;

/**
 * Interface for tjenesten for foreldrepenger.
 */
public interface ForeldrepengerServiceBi {

    @Cacheable(value = "foreldrePengerCache")
    ForeldrepengerListeResponse hentForeldrepengerListe(ForeldrepengerListeRequest request);
}

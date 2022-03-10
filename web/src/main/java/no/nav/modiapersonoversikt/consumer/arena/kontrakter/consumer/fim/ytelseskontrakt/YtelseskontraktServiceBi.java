package no.nav.modiapersonoversikt.consumer.arena.kontrakter.consumer.fim.ytelseskontrakt;

import no.nav.modiapersonoversikt.consumer.arena.kontrakter.consumer.fim.ytelseskontrakt.to.YtelseskontraktRequest;
import no.nav.modiapersonoversikt.consumer.arena.kontrakter.consumer.fim.ytelseskontrakt.to.YtelseskontraktResponse;
import org.springframework.cache.annotation.Cacheable;

/**
 * Interface for tjenesten for ytelseskontrakter.
 */
public interface YtelseskontraktServiceBi {
    @Cacheable(value = "ytelseskontrakter")
    YtelseskontraktResponse hentYtelseskontrakter(YtelseskontraktRequest request);
}

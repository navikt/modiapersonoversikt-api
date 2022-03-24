package no.nav.modiapersonoversikt.consumer.arena.ytelseskontrakt;

import no.nav.modiapersonoversikt.consumer.arena.ytelseskontrakt.domain.YtelseskontraktRequest;
import no.nav.modiapersonoversikt.consumer.arena.ytelseskontrakt.domain.YtelseskontraktResponse;
import org.springframework.cache.annotation.Cacheable;

/**
 * Interface for tjenesten for ytelseskontrakter.
 */
public interface YtelseskontraktService {
    @Cacheable(value = "ytelseskontrakter")
    YtelseskontraktResponse hentYtelseskontrakter(YtelseskontraktRequest request);
}

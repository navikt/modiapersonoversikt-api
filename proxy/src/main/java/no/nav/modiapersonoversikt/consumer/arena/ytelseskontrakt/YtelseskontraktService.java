package no.nav.modiapersonoversikt.consumer.arena.ytelseskontrakt;

import no.nav.modiapersonoversikt.arena.ytelseskontrakt.YtelseskontraktResponse;
import org.springframework.cache.annotation.Cacheable;

/**
 * Interface for tjenesten for ytelseskontrakter.
 */
public interface YtelseskontraktService {
    @Cacheable(value = "ytelseskontrakterCache")
    YtelseskontraktResponse hentYtelseskontrakter(YtelseskontraktRequest request);
}

package no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.pleiepenger;

import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.pleiepenger.mapping.to.PleiepengerListeRequest;
import no.nav.modiapersonoversikt.consumer.sykmeldingsperioder.consumer.pleiepenger.mapping.to.PleiepengerListeResponse;
import org.springframework.cache.annotation.Cacheable;

public interface PleiepengerService {

    @Cacheable(value = "pleiePengerCache")
    PleiepengerListeResponse hentPleiepengerListe(PleiepengerListeRequest request);
}

package no.nav.modiapersonoversiktproxy.consumer.infotrygd.pleiepenger;

import no.nav.modiapersonoversiktproxy.consumer.infotrygd.pleiepenger.mapping.to.PleiepengerListeRequest;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.pleiepenger.mapping.to.PleiepengerListeResponse;
import org.springframework.cache.annotation.Cacheable;

public interface PleiepengerService {

    @Cacheable(value = "pleiePengerCache")
    PleiepengerListeResponse hentPleiepengerListe(PleiepengerListeRequest request);
}

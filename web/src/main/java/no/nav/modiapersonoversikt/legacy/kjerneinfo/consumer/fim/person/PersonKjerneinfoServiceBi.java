package no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.to.HentSikkerhetstiltakRequest;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.GeografiskTilknytning;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.fakta.Sikkerhetstiltak;
import org.springframework.cache.annotation.Cacheable;

public interface PersonKjerneinfoServiceBi {

    @Cacheable(value = "kjerneinformasjonCache", key = "#hentKjerneinformasjonRequest.generateRequestId()")
    HentKjerneinformasjonResponse hentKjerneinformasjon(HentKjerneinformasjonRequest hentKjerneinformasjonRequest);

    @Cacheable(value = "kjerneinformasjonCache", key = "#ident.generateRequestId()")
	Sikkerhetstiltak hentSikkerhetstiltak(HentSikkerhetstiltakRequest ident);

    @Cacheable(value = "kjerneinformasjonCache", key = "#fodselsnummer")
    GeografiskTilknytning hentGeografiskTilknytning(String fodselsnummer);

}

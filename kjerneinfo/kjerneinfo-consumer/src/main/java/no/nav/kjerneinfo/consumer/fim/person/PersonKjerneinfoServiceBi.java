package no.nav.kjerneinfo.consumer.fim.person;

import no.nav.brukerprofil.domain.Bruker;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.kjerneinfo.consumer.fim.person.to.HentSikkerhetstiltakRequest;
import no.nav.kjerneinfo.domain.person.GeografiskTilknytning;
import no.nav.kjerneinfo.domain.person.fakta.Sikkerhetstiltak;
import org.springframework.cache.annotation.Cacheable;

public interface PersonKjerneinfoServiceBi {

    @Cacheable(value = "kjerneinformasjonCache", key = "#hentKjerneinformasjonRequest.generateRequestId()")
    HentKjerneinformasjonResponse hentKjerneinformasjon(HentKjerneinformasjonRequest hentKjerneinformasjonRequest);

    @Cacheable(value = "kjerneinformasjonCache", key = "#ident.generateRequestId()")
	Sikkerhetstiltak hentSikkerhetstiltak(HentSikkerhetstiltakRequest ident);

    @Cacheable(value = "kjerneinformasjonCache", key = "#fodselsnummer")
    GeografiskTilknytning hentGeografiskTilknytning(String fodselsnummer);

    Bruker hentBrukerprofil(String fodselsnummer);
}

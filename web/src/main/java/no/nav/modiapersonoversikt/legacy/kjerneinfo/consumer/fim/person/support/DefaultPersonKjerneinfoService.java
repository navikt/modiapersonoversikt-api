package no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.support;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.to.HentSikkerhetstiltakRequest;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.GeografiskTilknytning;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.fakta.Sikkerhetstiltak;
import no.nav.modiapersonoversikt.legacy.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;

public class DefaultPersonKjerneinfoService implements PersonKjerneinfoServiceBi {

    private final HentPersonService hentPersonService;
    private final HentSikkerhetstiltakService hentSikkerhetstiltakService;

    public DefaultPersonKjerneinfoService(PersonV3 service, KjerneinfoMapper mapper, Tilgangskontroll tilgangskontroll,
                                          final OrganisasjonEnhetV2Service organisasjonEnhetV2Service) {
        this.hentPersonService = new HentPersonService(service, mapper, organisasjonEnhetV2Service, tilgangskontroll);
        this.hentSikkerhetstiltakService = new HentSikkerhetstiltakService(service);
    }

    @Override
    public HentKjerneinformasjonResponse hentKjerneinformasjon(HentKjerneinformasjonRequest hentKjerneinformasjonRequest) {
        return hentPersonService.hentPerson(hentKjerneinformasjonRequest);
    }

    @Override
    public Sikkerhetstiltak hentSikkerhetstiltak(HentSikkerhetstiltakRequest ident) {
        return hentSikkerhetstiltakService.hentSikkerhetstiltak(ident.getIdent());
    }

    @Override
    public GeografiskTilknytning hentGeografiskTilknytning(String fodselsnummer) {
        return hentPersonService.hentGeografiskTilknytning(fodselsnummer);
    }
}

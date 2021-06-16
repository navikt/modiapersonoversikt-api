package no.nav.modiapersonoversikt.service.organisasjonenhet.kontaktinformasjon.service;

import no.nav.modiapersonoversikt.service.organisasjonenhet.kontaktinformasjon.domain.OrganisasjonEnhetKontaktinformasjon;

public interface OrganisasjonEnhetKontaktinformasjonService {

    OrganisasjonEnhetKontaktinformasjon hentKontaktinformasjon(String enhetId);

}

package no.nav.modiapersonoversikt.rest.enhet.model

import no.nav.modiapersonoversikt.service.organisasjonenhet.kontaktinformasjon.domain.OrganisasjonEnhetKontaktinformasjon

class EnhetKontaktinformasjon {

    var enhetId: String
    var enhetNavn: String
    var publikumsmottak: List<Publikumsmottak>

    constructor(organisasjonEnhetKontaktinformasjon: OrganisasjonEnhetKontaktinformasjon) {
        this.enhetId = organisasjonEnhetKontaktinformasjon.enhetId
        this.enhetNavn = organisasjonEnhetKontaktinformasjon.enhetNavn
        this.publikumsmottak = organisasjonEnhetKontaktinformasjon.kontaktinformasjon.publikumsmottak.map { Publikumsmottak(it) }
    }
}

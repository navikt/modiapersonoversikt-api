package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.enhet.model

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.domain.OrganisasjonEnhetKontaktinformasjon

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

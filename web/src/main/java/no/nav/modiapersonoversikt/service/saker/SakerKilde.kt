package no.nav.modiapersonoversikt.service.saker

import no.nav.modiapersonoversikt.api.domain.saker.Sak

interface SakerKilde {
    val kildeNavn: String
    fun leggTilSaker(fnr: String, saker: MutableList<Sak>)
}

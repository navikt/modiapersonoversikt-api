package no.nav.modiapersonoversikt.service.saker

interface SakerKilde {
    val kildeNavn: String
    fun leggTilSaker(fnr: String, saker: MutableList<Sak>)
}

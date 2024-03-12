package no.nav.modiapersonoversikt.service.journalforingsaker

interface SakerKilde {
    val kildeNavn: String

    fun leggTilSaker(
        fnr: String,
        saker: MutableList<JournalforingSak>,
    )
}

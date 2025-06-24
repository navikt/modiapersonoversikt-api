package no.nav.modiapersonoversikt.service.journalforingsaker.kilder

import no.nav.modiapersonoversikt.consumer.aap.AapApi
import no.nav.modiapersonoversikt.service.journalforingsaker.JournalforingSak
import no.nav.modiapersonoversikt.service.journalforingsaker.SakerKilde

internal class AapSaker(
    private val aapApi: AapApi,
) : SakerKilde {
    override val kildeNavn: String
        get() = "AAP"

    override fun leggTilSaker(
        fnr: String,
        saker: MutableList<JournalforingSak>,
    ) {
        saker.plus(aapApi.hentAapSaker(fnr))
    }
}

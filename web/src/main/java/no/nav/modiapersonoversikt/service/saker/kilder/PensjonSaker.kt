package no.nav.modiapersonoversikt.service.saker.kilder

import no.nav.modiapersonoversikt.legacy.api.domain.saker.Sak
import no.nav.modiapersonoversikt.service.pensjonsak.PsakService
import no.nav.modiapersonoversikt.service.saker.SakerKilde

internal class PensjonSaker(val psakService: PsakService) : SakerKilde {
    override val kildeNavn: String
        get() = "PESYS"

    override fun leggTilSaker(fnr: String, saker: MutableList<Sak>) {
        saker.addAll(psakService.hentSakerFor(fnr))
    }
}

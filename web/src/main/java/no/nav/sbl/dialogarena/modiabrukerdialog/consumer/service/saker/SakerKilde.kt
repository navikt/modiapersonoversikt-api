package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.saker.Sak

interface SakerKilde {
    val kildeNavn: String
    fun leggTilSaker(fnr: String, saker: MutableList<Sak>)
}

package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.psak.PsakService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.SakerKilde

internal class PensjonSaker(val psakService: PsakService) : SakerKilde {
    override val kildeNavn: String
        get() = "PESYS"

    override fun leggTilSaker(fnr: String, saker: MutableList<Sak>) {
        saker.addAll(psakService.hentSakerFor(fnr))
    }
}

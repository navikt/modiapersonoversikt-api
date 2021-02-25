package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.kodeverk

import no.nav.kjerneinfo.common.domain.Kodeverdi
import no.nav.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.naudit.Audit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/kodeverk/{kodeverkRef}")
class KodeverkController @Autowired constructor(
    private val tilgangskontroll: Tilgangskontroll,
    private val kodeverkManager: KodeverkmanagerBi
) {
    @GetMapping
    fun hentKodeverk(@PathVariable("kodeverkRef") kodeverkRef: String) =
        tilgangskontroll
            .check(Policies.tilgangTilModia)
            .get(Audit.skipAuditLog()) {
                mapOf(
                    "kodeverk" to kodeverkManager
                        .getKodeverkList(kodeverkRef, "nb")
                        .map(::Kode)
                )
            }
}

data class Kode(val kodeRef: String, val beskrivelse: String) {
    constructor(kodeverdi: Kodeverdi) : this(kodeverdi.kodeRef, kodeverdi.beskrivelse)
}

package no.nav.modiapersonoversikt.rest.kodeverk

import no.nav.modiapersonoversikt.consumer.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain.Kodeverdi
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.enhetligkodeverk.KodeverkConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest")
class KodeverkController @Autowired constructor(
    private val tilgangskontroll: Tilgangskontroll,
    private val kodeverkManager: KodeverkmanagerBi,
    private val kodeverk: EnhetligKodeverk.Service
) {
    @GetMapping("/kodeverk/{kodeverkRef}")
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

    @GetMapping("/v2/kodeverk/{kodeverkRef}")
    fun hentEnhetligKodeverk(@PathVariable("kodeverkRef") kodeverkRef: KodeverkConfig): EnhetligKodeverk.Kodeverk {
        return tilgangskontroll
            .check(Policies.tilgangTilModia)
            .get(Audit.skipAuditLog()) {
                kodeverk.hentKodeverk(kodeverkRef)
            }
    }
}

data class Kode(val kodeRef: String, val beskrivelse: String) {
    constructor(kodeverdi: Kodeverdi) : this(kodeverdi.kodeRef, kodeverdi.beskrivelse)
}

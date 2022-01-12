package no.nav.modiapersonoversikt.rest.kodeverk

import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
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
    private val kodeverk: EnhetligKodeverk.Service
) {
    @GetMapping("/v2/kodeverk/{kodeverkRef}")
    fun hentEnhetligKodeverk(@PathVariable("kodeverkRef") kodeverkRef: KodeverkConfig): EnhetligKodeverk.Kodeverk {
        return tilgangskontroll
            .check(Policies.tilgangTilModia)
            .get(Audit.skipAuditLog()) {
                kodeverk.hentKodeverk(kodeverkRef)
            }
    }
}

package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest

import no.nav.sbl.dialogarena.abac.AbacClient
import no.nav.sbl.dialogarena.abac.AbacResponse
import no.nav.sbl.dialogarena.abac.Decision
import no.nav.sbl.dialogarena.abac.DenyCause
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.AbacPolicies
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/tilgang")
class TilgangController @Autowired constructor(private val abacClient: AbacClient) {

    @GetMapping("/{fnr}")
    fun harTilgang(@PathVariable("fnr") fnr: String): TilgangDTO {
        return abacClient
                .evaluate(AbacPolicies.tilgangTilBruker(fnr))
                .makeResponse()
    }

    @GetMapping
    fun harTilgang(): TilgangDTO {
        return abacClient
                .evaluate(AbacPolicies.tilgangTilModia())
                .makeResponse()
    }

}

class TilgangDTO(val harTilgang: Boolean, val ikkeTilgangArsak: DenyCause?)

internal fun AbacResponse.makeResponse(): TilgangDTO {
    if (this.getBiasedDecision(Decision.Deny) == Decision.Permit) {
        return TilgangDTO(true, null)
    }

    return TilgangDTO(false, this.getCause())
}

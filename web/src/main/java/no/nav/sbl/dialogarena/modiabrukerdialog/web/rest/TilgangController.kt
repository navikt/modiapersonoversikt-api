package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest

import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.SubjectHandler
import no.nav.sbl.dialogarena.abac.AbacClient
import no.nav.sbl.dialogarena.abac.AbacResponse
import no.nav.sbl.dialogarena.abac.Decision
import no.nav.sbl.dialogarena.abac.DenyCause
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.AbacPolicies
import java.text.ParseException
import java.util.*
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

    @GetMapping("/auth")
    fun authIntropection(): AuthIntropectionDTO {
        return SubjectHandler.getSsoToken()
                .map(SsoToken::getExpirationDate)
                .orElse(AuthIntropectionDTO.INVALID)
    }

}

class TilgangDTO(val harTilgang: Boolean, val ikkeTilgangArsak: DenyCause?)
class AuthIntropectionDTO(val expirationDate: Long) {
    companion object {
        val INVALID = AuthIntropectionDTO(-1)
    }
}

internal fun SsoToken.getExpirationDate(): AuthIntropectionDTO {
    return when(val exp: Any? = this.attributes["exp"]) {
        null -> AuthIntropectionDTO.INVALID
        is Date -> AuthIntropectionDTO(exp.time)
        is Number -> AuthIntropectionDTO(exp.toLong() * 1000) // Er epoch-seconds, men vil ha epoch-ms
        else -> throw ParseException("The \"exp\" claim is not a Date", 0)
    }
}

internal fun AbacResponse.makeResponse(): TilgangDTO {
    if (this.getBiasedDecision(Decision.Deny) == Decision.Permit) {
        return TilgangDTO(true, null)
    }

    return TilgangDTO(false, this.getCause())
}

package no.nav.modiapersonoversikt.rest

import com.nimbusds.jwt.JWTClaimsSet
import no.nav.common.auth.context.AuthContextHolderThreadLocal
import no.nav.modiapersonoversikt.consumer.abac.AbacClient
import no.nav.modiapersonoversikt.consumer.abac.AbacResponse
import no.nav.modiapersonoversikt.consumer.abac.Decision
import no.nav.modiapersonoversikt.consumer.abac.DenyCause
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.AbacPolicies
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/rest/tilgang")
class TilgangController @Autowired constructor(private val abacClient: AbacClient) {
    val audit = Audit.describe<String>(Audit.Action.READ, AuditResources.Person.Tilgang) {
        listOf(AuditIdentifier.FNR to it)
    }

    @GetMapping("/{fnr}")
    fun harTilgang(@PathVariable("fnr") fnr: String): TilgangDTO {
        return abacClient
            .evaluate(AbacPolicies.tilgangTilBruker(fnr))
            .makeResponse()
            .logAudit(audit, fnr)
    }

    @GetMapping
    fun harTilgang(): TilgangDTO {
        return abacClient
            .evaluate(AbacPolicies.tilgangTilModia())
            .makeResponse()
    }

    @GetMapping("/auth")
    fun authIntropection(): AuthIntropectionDTO {
        return AuthContextHolderThreadLocal.instance()
            .idTokenClaims
            .map(JWTClaimsSet::getExpirationDate)
            .orElse(AuthIntropectionDTO.INVALID)
    }
}

class TilgangDTO(val harTilgang: Boolean, val ikkeTilgangArsak: DenyCause?)
class AuthIntropectionDTO(val expirationDate: Long) {
    companion object {
        val INVALID = AuthIntropectionDTO(-1)
    }
}

private fun TilgangDTO.logAudit(audit: Audit.AuditDescriptor<String>, fnr: String): TilgangDTO {
    when (this.harTilgang) {
        true -> audit.log(fnr)
        else -> audit.denied("Ikke tilgang til $fnr, årsak: ${this.ikkeTilgangArsak}")
    }
    return this
}

internal fun JWTClaimsSet.getExpirationDate(): AuthIntropectionDTO {
    return when (val exp: Date? = this.expirationTime) {
        null -> AuthIntropectionDTO.INVALID
        else -> AuthIntropectionDTO(exp.time)
    }
}

internal fun AbacResponse.makeResponse(): TilgangDTO {
    if (this.getBiasedDecision(Decision.Deny) == Decision.Permit) {
        return TilgangDTO(true, null)
    }

    return TilgangDTO(false, this.getCause())
}

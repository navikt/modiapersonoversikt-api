package no.nav.modiapersonoversikt.rest

import com.nimbusds.jwt.JWTClaimsSet
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.kabac.Decision
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/rest/tilgang")
class TilgangController @Autowired constructor(private val tilgangskontroll: Tilgangskontroll) {
    val audit = Audit.describe<String>(Audit.Action.READ, AuditResources.Person.Tilgang) {
        listOf(AuditIdentifier.FNR to it)
    }

    @GetMapping("/{fnr}")
    fun harTilgang(@PathVariable("fnr") fnr: String): TilgangDTO {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(fnr)))
            .getDecision()
            .makeResponse()
            .logAudit(audit, fnr)
    }

    @GetMapping
    fun harTilgang(): TilgangDTO {
        return tilgangskontroll
            .check(Policies.tilgangTilModia())
            .getDecision()
            .makeResponse()
    }

    @GetMapping("/auth")
    fun authIntropection(): AuthIntropectionDTO {
        return AuthContextUtils.getClaims()
            .map(JWTClaimsSet::getExpirationDate)
            .orElse(AuthIntropectionDTO.INVALID)
    }
}

class TilgangDTO(val harTilgang: Boolean, val ikkeTilgangArsak: Decision.DenyCause?)
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

internal fun Decision.makeResponse(): TilgangDTO {
    return when (val biased = this.withBias(Decision.Type.DENY)) {
        is Decision.Permit -> TilgangDTO(true, null)
        is Decision.Deny -> TilgangDTO(false, biased.cause)
        is Decision.NotApplicable -> TilgangDTO(false, Decision.NO_APPLICABLE_POLICY_FOUND)
    }
}

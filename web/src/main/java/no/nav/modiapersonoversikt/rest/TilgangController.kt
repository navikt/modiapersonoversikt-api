package no.nav.modiapersonoversikt.rest

import com.nimbusds.jwt.JWTClaimsSet
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.personoversikt.common.kabac.Decision
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/rest/tilgang")
class TilgangController @Autowired constructor(private val tilgangskontroll: Tilgangskontroll) {
    private val audit = Audit.describe<String>(Audit.Action.READ, AuditResources.Person.Tilgang) {
        listOf(AuditIdentifier.FNR to it)
    }
    private val enhetTrace = Audit.describe<String>(Audit.Action.READ, AuditResources.Person.EnhetTrace) {
        listOf(AuditIdentifier.ENHET_ID to it)
    }

    @GetMapping("/{fnr}")
    fun harTilgang(
        @PathVariable("fnr") fnr: String,
        @RequestParam("enhet", required = false) enhet: String?,
        request: HttpServletRequest
    ): TilgangDTO {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(fnr)))
            .getDecision()
            .makeResponse()
            .logAudit(audit, fnr)
            .also {
                enhetTrace.log(enhet ?: "IKKE SATT")
            }
    }

    @GetMapping
    fun harTilgang(): TilgangDTO {
        return tilgangskontroll
            .check(Policies.tilgangTilModia)
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

private fun <T> TilgangDTO.logAudit(audit: Audit.AuditDescriptor<T>, data: T): TilgangDTO {
    when (this.harTilgang) {
        true -> audit.log(data)
        else -> audit.denied("Ikke tilgang til $data, årsak: ${this.ikkeTilgangArsak}")
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

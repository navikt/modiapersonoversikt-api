package no.nav.modiapersonoversikt.rest

import com.nimbusds.jwt.JWTClaimsSet
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.commondomain.FnrRequest
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.personoversikt.common.kabac.Decision
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/rest/tilgang")
class TilgangController @Autowired constructor(
    private val tilgangskontroll: Tilgangskontroll,
    private val pdlOppslagService: PdlOppslagService
) {
    private val audit = Audit.describe<String>(Audit.Action.READ, AuditResources.Person.Tilgang) {
        listOf(AuditIdentifier.FNR to it)
    }
    private val enhetTrace = Audit.describe<String>(Audit.Action.READ, AuditResources.Person.EnhetTrace) {
        listOf(AuditIdentifier.ENHET_ID to it)
    }

    @PostMapping()
    fun harTilgang(
        @RequestBody fnrRequest: FnrRequest,
        @RequestParam("enhet", required = false) enhet: String?,
        request: HttpServletRequest
    ): TilgangDTO {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(fnrRequest.fnr)))
            .getDecision()
            .makeResponse()
            .sjekkAktivFolkeregistrIden(fnrRequest.fnr)
            .logAudit(audit, fnrRequest.fnr)
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

    private fun TilgangDTO.sjekkAktivFolkeregistrIden(fnr: String): TilgangDTO {
        return if (this.harTilgang) {
            val aktivIdent = pdlOppslagService.hentFolkeregisterIdenter(fnr)
                ?.identer?.find { !it.historisk }
            this.copy(aktivIdent = aktivIdent?.ident)
        } else {
            this
        }
    }
}

data class TilgangDTO(
    val harTilgang: Boolean,
    val ikkeTilgangArsak: Decision.DenyCause?,
    val aktivIdent: String?
)

class AuthIntropectionDTO(val expirationDate: Long) {
    companion object {
        val INVALID = AuthIntropectionDTO(-1)
    }
}

internal fun <T> TilgangDTO.logAudit(audit: Audit.AuditDescriptor<T>, data: T): TilgangDTO {
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
        is Decision.Permit -> TilgangDTO(true, null, null)
        is Decision.Deny -> TilgangDTO(false, biased.cause, null)
        is Decision.NotApplicable -> TilgangDTO(false, Decision.NO_APPLICABLE_POLICY_FOUND, null)
    }
}

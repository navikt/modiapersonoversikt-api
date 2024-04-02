package no.nav.modiapersonoversikt.rest

import com.nimbusds.jwt.JWTClaimsSet
import jakarta.servlet.http.HttpServletRequest
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.commondomain.FnrRequest
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/rest/v2/tilgang")
class TilgangControllerV2
    @Autowired
    constructor(
        private val tilgangskontroll: Tilgangskontroll,
        private val pdlOppslagService: PdlOppslagService,
    ) {
        private val audit =
            Audit.describe<String>(Audit.Action.READ, AuditResources.Person.Tilgang) {
                listOf(AuditIdentifier.FNR to it)
            }
        private val enhetTrace =
            Audit.describe<String>(Audit.Action.READ, AuditResources.Person.EnhetTrace) {
                listOf(AuditIdentifier.ENHET_ID to it)
            }

        @PostMapping()
        fun harTilgang(
            @RequestBody fnrRequest: FnrRequest,
            @RequestParam("enhet", required = false) enhet: String?,
            request: HttpServletRequest,
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
                val aktivIdent =
                    pdlOppslagService.hentFolkeregisterIdenter(fnr)
                        ?.identer?.find { !it.historisk }
                this.copy(aktivIdent = aktivIdent?.ident)
            } else {
                this
            }
        }
    }

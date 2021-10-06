package no.nav.modiapersonoversikt.rest.internal

import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.SubjectHandler
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/internal")
class InternalController @Autowired constructor(
    private val systemUserTokenProvider: SystemUserTokenProvider,
    private val tilgangskontroll: Tilgangskontroll
) {
    data class Tokens(val user: String, val system: String)

    @GetMapping("/tokens")
    fun hentSystembrukerToken(): Tokens {
        return tilgangskontroll
            .check(Policies.tilgangTilModia)
            .check(Policies.kanBrukeInternal)
            .get(Audit.describe(Audit.Action.READ, AuditResources.Introspection.Tokens)) {
                Tokens(
                    user = SubjectHandler.getSsoToken(SsoToken.Type.OIDC).orElse("null"),
                    system = systemUserTokenProvider.systemUserToken
                )
            }
    }
}

package no.nav.modiapersonoversikt.rest.ldap

import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.consumer.ldap.LDAPService
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.READ
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources.Saksbehandler
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/veileder")
class LdapController @Autowired
constructor(private val ldapService: LDAPService, private val tilgangskontroll: Tilgangskontroll) {
    @GetMapping("/roller")
    fun hentRollerForInnloggetVeileder(): Map<String, List<String>> {
        return tilgangskontroll
            .check(Policies.tilgangTilModia)
            .get(Audit.describe(READ, Saksbehandler.Roller)) {
                val ident = AuthContextUtils.requireIdent()
                mapOf("roller" to ldapService.hentRollerForVeileder(NavIdent(ident)))
            }
    }
}

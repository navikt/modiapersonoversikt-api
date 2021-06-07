package no.nav.modiapersonoversikt.rest.ldap

import no.nav.common.auth.subject.SubjectHandler
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.READ
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources.Saksbehandler
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.legacy.api.service.ldap.LDAPService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/veileder")
class LdapController @Autowired
constructor(private val ldapService: LDAPService, private val tilgangskontroll: Tilgangskontroll) {
    @GetMapping("/roller")
    fun hentRollerForInnloggetVeileder(): Map<String, MutableList<String>> {
        return tilgangskontroll
            .check(Policies.tilgangTilModia)
            .get(Audit.describe(READ, Saksbehandler.Roller)) {
                val ident = SubjectHandler.getIdent().get()
                mapOf("roller" to ldapService.hentRollerForVeileder(ident))
            }
    }
}

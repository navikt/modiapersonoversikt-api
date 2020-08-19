package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.ldap

import no.nav.common.auth.subject.SubjectHandler
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.naudit.Audit
import no.nav.sbl.dialogarena.naudit.Audit.Action.READ
import no.nav.sbl.dialogarena.naudit.AuditResources.Saksbehandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/veileder")
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

package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.ldap

import no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Tilgangskontroll
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/veileder")
class LdapController @Inject
constructor(private val ldapService: LDAPService, private val tilgangskontroll: Tilgangskontroll) {

    @GET
    @Path("/roller")
    @Produces(MediaType.APPLICATION_JSON)
    fun hentRollerForInnloggetVeileder(): Map<String, MutableList<String>> {
        return tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get {
                    val ident = getSubjectHandler().uid
                    mapOf("roller" to ldapService.hentRollerForVeileder(ident))
                }
    }
}
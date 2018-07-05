package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.ldap

import no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.visFeature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.unleash.UnleashService
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/veileder")
class LdapController @Inject
constructor(private val ldapService: LDAPService, private val unleashService: UnleashService) {

    @GET
    @Path("/roller")
    @Produces(MediaType.APPLICATION_JSON)
    fun hentRollerForInnloggetVeileder(): Map<String, MutableList<String>> {
        check(unleashService.isEnabled(Feature.NYTT_VISITTKORT_UNLEASH.propertyKey))

        val ident = getSubjectHandler().uid
        return mapOf("roller" to ldapService.hentRollerForVeileder(ident))
    }
}
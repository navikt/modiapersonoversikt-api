package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.brukerprofil

import no.nav.brukerdialog.security.context.SubjectHandler
import no.nav.kjerneinfo.consumer.fim.behandleperson.DefaultBehandlePersonService
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature.PERSON_REST_API
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.visFeature
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.brukerprofil.domain.EndreNavnRequest
import no.nav.tjeneste.virksomhet.behandleperson.v1.meldinger.WSEndreNavnRequest
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.ok

const val ENDRE_NAVN_ROLLE = "0000-GA-BD06_EndreNavn"

@Path("/brukerprofil/{fnr}")
@Produces(APPLICATION_JSON)
class BrukerprofilController @Inject constructor(private val behandlePersonService: DefaultBehandlePersonService, private val kjerneinfoService: PersonKjerneinfoServiceBi, private val ldapService: LDAPService) {

    @POST
    @Path("/navn")
    @Consumes(APPLICATION_JSON)
    fun endreNavn(@PathParam("fnr") fødselsnummer: String, endreNavnRequest: EndreNavnRequest): Response {
        check(visFeature(PERSON_REST_API))
        verifyTilgang()

        val kjerneinformasjon = kjerneinfoService.hentKjerneinformasjon(HentKjerneinformasjonRequest(fødselsnummer))

        if (!kjerneinformasjon.person.kanEndreNavn()) {
            throw ForbiddenException("Det er ikke lovlig å endre navn til person med fødselsnummer: $fødselsnummer")
        }

        behandlePersonService.endreNavn(WSEndreNavnRequest()
                .withFnr(fødselsnummer)
                .withFornavn(endreNavnRequest.fornavn)
                .withMellomnavn(endreNavnRequest.mellomnavn)
                .withEtternavn(endreNavnRequest.etternavn))

        return ok().build()
    }

    private fun verifyTilgang() {
        val consumerId = SubjectHandler.getSubjectHandler().uid
        if (!ldapService.saksbehandlerHarRolle(consumerId, ENDRE_NAVN_ROLLE)) {
            throw ForbiddenException("Saksbehandler $consumerId har ikke rollen $ENDRE_NAVN_ROLLE og kan derfor ikke endre navn")
        }
    }

}

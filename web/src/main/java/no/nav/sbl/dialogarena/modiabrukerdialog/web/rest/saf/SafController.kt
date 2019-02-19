package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.saf

import no.nav.brukerdialog.security.context.SubjectHandler
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.sts.StsServiceImpl
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.RestConstants.*
import no.nav.sbl.rest.RestUtils
import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.client.Entity
import javax.ws.rs.core.HttpHeaders.AUTHORIZATION
import javax.ws.rs.core.HttpHeaders.CONTENT_TYPE
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.ok
import javax.ws.rs.core.Response.status
import javax.ws.rs.core.Response.Status.*

@Path("/saf")
@Produces(APPLICATION_JSON)
class SafController @Inject constructor(private val stsService: StsServiceImpl) {

    @POST
    @Path("/")
    @Consumes(APPLICATION_JSON)
    fun hentSaker(safRequest: SafRequest): Response {
        val veilederOidcToken = SubjectHandler.getSubjectHandler().internSsoToken
        val escapedQuery = safRequest.query.replace("\"", "\\\"")
        val result = gjorSporring(SAF_BASEURL, veilederOidcToken, escapedQuery)

        when (result.status) {
            //TODO: her må det i minste logges litt ved feil, kanskje utvide med flere statuskoder.
            200 -> return ok().entity(result.entity).build()
            404 -> return status(NOT_FOUND).build()
            else -> return status(INTERNAL_SERVER_ERROR).build()
        }
    }

    private fun gjorSporring(url: String, veilederOidcToken: String, query: String): Response {
        return RestUtils.withClient { client ->
            client
                    .target(url)
                    .request()
                    .header(AUTHORIZATION, AUTH_METHOD_BEARER + AUTH_SEPERATOR + veilederOidcToken)
                    .header(CONTENT_TYPE, APPLICATION_JSON)
                    .post(Entity.entity("{\"query\":\"$query\"}", APPLICATION_JSON))
        }
    }
}
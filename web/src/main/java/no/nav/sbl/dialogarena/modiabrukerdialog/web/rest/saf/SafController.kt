package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.saf

import no.nav.brukerdialog.security.context.SubjectHandler
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.RestConstants.*
import no.nav.sbl.rest.RestUtils
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.client.Entity
import javax.ws.rs.core.HttpHeaders.AUTHORIZATION
import javax.ws.rs.core.HttpHeaders.CONTENT_TYPE
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR
import javax.ws.rs.core.Response.Status.NOT_FOUND
import javax.ws.rs.core.Response.ok
import javax.ws.rs.core.Response.status

@Path("/saf")
@Produces(APPLICATION_JSON)
class SafController {

    @POST
    @Path("/")
    @Consumes(APPLICATION_JSON)
    fun hentSaker(graphQLQuery: GrapQLQuery): Response {
        val veilederOidcToken = SubjectHandler.getSubjectHandler().internSsoToken
        val result = gjorSporring(SAF_BASEURL, veilederOidcToken, graphQLQuery)

        when (result.status) {
            //TODO: her må det i minste logges litt ved feil, kanskje utvide med flere statuskoder.
            200 -> return ok().entity(result.entity).build()
            404 -> return status(NOT_FOUND).build()
            else -> return status(INTERNAL_SERVER_ERROR).build()
        }
    }

    private fun gjorSporring(url: String, veilederOidcToken: String, query: GrapQLQuery): Response {
        val jsonQuery = graphQLtoJson(query)
        return RestUtils.withClient { client ->
            client
                    .target(url)
                    .request()
                    .header(AUTHORIZATION, AUTH_METHOD_BEARER + AUTH_SEPERATOR + veilederOidcToken)
                    .header(CONTENT_TYPE, APPLICATION_JSON)
                    .post(Entity.entity(jsonQuery, APPLICATION_JSON))
        }
    }

    private fun graphQLtoJson(grapQLQuery: GrapQLQuery): String {
        val escapedQuery = grapQLQuery.query.replace("\"", "\\\"")
        return "{\"query\":\"$escapedQuery\"}"
    }
}
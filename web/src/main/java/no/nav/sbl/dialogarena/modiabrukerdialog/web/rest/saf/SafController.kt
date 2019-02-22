package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.saf

import no.nav.brukerdialog.security.context.SubjectHandler
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.RestConstants.*
import no.nav.sbl.rest.RestUtils
import javax.ws.rs.*
import javax.ws.rs.client.Client
import javax.ws.rs.client.Entity
import javax.ws.rs.client.Invocation
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
    @Path("/hentSaker/")
    @Consumes(APPLICATION_JSON)
    fun hentSaker(graphQLQuery: GrapQLQuery): Response {
        val jsonQuery = graphQLtoJson(graphQLQuery)

        val result = RestUtils.withClient { client ->
            veilederAutorisertClient(client, SAF_GRAPHQL_BASEURL).post(Entity.entity(jsonQuery, APPLICATION_JSON))
        }

        return when (result.status) {
            //TODO: her må det i minste logges litt ved feil, kanskje utvide med flere statuskoder.
            200 -> ok().entity(result.entity).build()
            404 -> status(NOT_FOUND).build()
            else -> status(INTERNAL_SERVER_ERROR).build()
        }
    }

    @GET
    @Path("/hentDokument/{journalpostId}/{dokumentInfoId}/{variantFormat}/")
    @Consumes(APPLICATION_JSON)
    @Produces("application/pdf")
    fun hentDokument(
            @PathParam("journalpostId") journalpostId: String,
            @PathParam("dokumentInfoId") dokumentInfoId: String,
            @PathParam("variantFormat") variantFormat: String): Response {

        val url = lagHentDokumentURL(journalpostId, dokumentInfoId, variantFormat)

        val result = RestUtils.withClient { client ->
            veilederAutorisertClient(client, url).get()
        }

        return when (result.status) {
            //TODO: her må det i minste logges litt ved feil, kanskje utvide med flere statuskoder.
            200 -> ok().entity(result.entity).build()
            404 -> status(NOT_FOUND).build()
            else -> status(INTERNAL_SERVER_ERROR).build()
        }
    }


}

private fun veilederAutorisertClient(client: Client, url: String): Invocation.Builder {
    val veilederOidcToken = SubjectHandler.getSubjectHandler().internSsoToken
    return client
            .target(url)
            .request()
            .header(AUTHORIZATION, AUTH_METHOD_BEARER + AUTH_SEPERATOR + veilederOidcToken)
            .header(CONTENT_TYPE, APPLICATION_JSON)
}

private fun graphQLtoJson(grapQLQuery: GrapQLQuery): String {
    val escapedQuery = grapQLQuery.query.replace("\"", "\\\"")
    return "{\"query\":\"$escapedQuery\"}"
}

private fun lagHentDokumentURL(journalpostId: String, dokumentInfoId: String, variantFormat: String) =
        SAF_HENTDOKUMENT_BASEURL + String.format(
                "/%s/%s/%s/",
                journalpostId,
                dokumentInfoId,
                variantFormat)
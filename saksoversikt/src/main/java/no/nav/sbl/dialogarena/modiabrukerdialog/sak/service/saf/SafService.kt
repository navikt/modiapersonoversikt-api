package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.saf

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.brukerdialog.security.context.SubjectHandler
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.DokumentMetadata
import no.nav.sbl.rest.RestUtils
import javax.ws.rs.InternalServerErrorException
import javax.ws.rs.NotFoundException
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

val SAF_GRAPHQL_BASEURL = System.getProperty("saf.graphql.url")
val SAF_HENTDOKUMENT_BASEURL = System.getProperty("saf.hentdokument.url")

class SafService {

    fun hentJournalposter(fnr: String): List<DokumentMetadata> {

        val jsonQuery = dokumentoversiktBrukerJsonQuery(fnr)

        return RestUtils.withClient { client ->
            val response = veilederAutorisertClient(client, SAF_GRAPHQL_BASEURL)
                    .post(Entity.entity(jsonQuery, APPLICATION_JSON))

            håndterStatus(response)
        }
    }

    fun hentDokument(journalpostId: String, dokumentInfoId: String, variantFormat: String): Response {

        val url = lagHentDokumentURL(journalpostId, dokumentInfoId, variantFormat)

        val result = RestUtils.withClient { client ->
            veilederAutorisertClient(client, url).get()
        }

        return when (result.status) {
            200 -> ok().entity(result.entity).build()
            404 -> status(NOT_FOUND).build()
            else -> status(INTERNAL_SERVER_ERROR).build()
        }
    }
}

private fun håndterStatus(response: Response): List<DokumentMetadata> =
        when (response.status) {
            200 -> handterResponse(response)
            404 -> throw NotFoundException("Responskode 404 fra SAF - dokumentoversiktBruker")
            500 -> throw InternalServerErrorException("Responskode 500 fra SAF - dokumentoversiktBruker")
            else -> throw RuntimeException("Ukjent feil i kall mot SAF - dokumentoversiktBruker")
        }

private fun handterResponse(response: Response): List<DokumentMetadata> {
    val safSakerResponse = safResponsFraResponse(response)

    safSakerResponse.errors?.let { lagErrorOgKast(safSakerResponse.errors) }

    return getDokumentMetadata(safSakerResponse)
}

private fun safResponsFraResponse(response: Response): SafDokumentResponse {
    val rawJson = response.readEntity(String::class.java)
    val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())

    return try {
        (mapper.readValue(rawJson))
    } catch (e: Exception) {
        throw RuntimeException("Feil i mapping for SAF query hentJournalposter, sjekk om query og objekt er like", e)
    }
}

private fun lagErrorOgKast(errors: List<SafError>) {
    val msg = errors
            .map { err ->
                err.message +
                        " Lokasjon: "+ err.locations.toString()
            }.reduce { a, b -> "$a \n $b" }
    
    throw InternalServerErrorException("Feil i kall mot SAF - dokumentoversiktBruker \n Mottat feilmelding: $msg")
}

private fun getDokumentMetadata(safDokumentResponse: SafDokumentResponse): List<DokumentMetadata> =
        safDokumentResponse.data?.dokumentoversiktBruker?.journalposter
                .orEmpty()
                .map { journalpost -> DokumentMetadata().fraSafJournalpost(journalpost) }


private fun veilederAutorisertClient(client: Client, url: String): Invocation.Builder {
    val AUTH_METHOD_BEARER = "Bearer"
    val AUTH_SEPERATOR = " "

    val veilederOidcToken = SubjectHandler.getSubjectHandler().internSsoToken
    return client
            .target(url)
            .request()
            .header(AUTHORIZATION, AUTH_METHOD_BEARER + AUTH_SEPERATOR + veilederOidcToken)
            .header(CONTENT_TYPE, APPLICATION_JSON)
}

private fun lagHentDokumentURL(journalpostId: String, dokumentInfoId: String, variantFormat: String) =
        SAF_HENTDOKUMENT_BASEURL + String.format(
                "/%s/%s/%s/",
                journalpostId,
                dokumentInfoId,
                variantFormat)
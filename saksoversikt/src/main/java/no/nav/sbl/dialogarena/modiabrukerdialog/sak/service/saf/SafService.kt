package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.saf

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.brukerdialog.security.context.SubjectHandler
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Baksystem
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Dokument
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.DokumentMetadata
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere.ResultatWrapper
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere.TjenesteResultatWrapper
import no.nav.sbl.rest.RestUtils
import org.slf4j.LoggerFactory
import javax.ws.rs.client.Client
import javax.ws.rs.client.Entity
import javax.ws.rs.client.Invocation
import javax.ws.rs.core.HttpHeaders.AUTHORIZATION
import javax.ws.rs.core.HttpHeaders.CONTENT_TYPE
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status.fromStatusCode

val SAF_GRAPHQL_BASEURL = System.getProperty("saf.graphql.url")
val SAF_HENTDOKUMENT_BASEURL = System.getProperty("saf.hentdokument.url")

private val LOG = LoggerFactory.getLogger(SafService::class.java)

class SafService {
    fun hentJournalposter(fnr: String): ResultatWrapper<List<DokumentMetadata>> {
        val jsonQuery = dokumentoversiktBrukerJsonQuery(fnr)

        return RestUtils.withClient { client ->
            val response = veilederAutorisertClient(client, SAF_GRAPHQL_BASEURL)
                    .post(Entity.entity(jsonQuery, APPLICATION_JSON))

            håndterStatus(response)
        }
    }

    fun hentDokument(journalpostId: String, dokumentInfoId: String, variantFormat: Dokument.Variantformat): TjenesteResultatWrapper {
        val url = lagHentDokumentURL(journalpostId, dokumentInfoId, variantFormat)

        return RestUtils.withClient { client ->
            val response = veilederAutorisertClient(client, url).get()
            when (response.status) {
                200 -> TjenesteResultatWrapper(response.readEntity(ByteArray::class.java))
                else -> håndterDokumentFeilKoder(response.status)
            }
        }
    }
}

private fun håndterStatus(response: Response): ResultatWrapper<List<DokumentMetadata>> =
        when (response.status) {
            200 -> håndterResponse(response)
            else -> {
                håndterJournalpostFeilKoder(response.status)
                ResultatWrapper(emptyList(), setOf(Baksystem.SAF))
            }
        }

private fun håndterResponse(response: Response): ResultatWrapper<List<DokumentMetadata>> {
    val safDokumentResponse = safDokumentResponsFraResponse(response)

    safDokumentResponse.errors?.also { logJournalpostErrors(safDokumentResponse.errors) }

    return ResultatWrapper(
            getDokumentMetadata(safDokumentResponse),
            safDokumentResponse.errors?.let { setOf(Baksystem.SAF) }.orEmpty()
    )
}

private fun safDokumentResponsFraResponse(response: Response): SafDokumentResponse {
    val rawJson = response.readEntity(String::class.java)
    val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())

    return try {
        (mapper.readValue(rawJson))
    } catch (e: Exception) {
        throw RuntimeException("Feil i mapping for SAF query hentJournalposter, sjekk om query og objekt er like", e)
    }
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

private fun lagHentDokumentURL(journalpostId: String, dokumentInfoId: String, variantFormat: Dokument.Variantformat) =
        SAF_HENTDOKUMENT_BASEURL + String.format(
                "/%s/%s/%s/",
                journalpostId,
                dokumentInfoId,
                variantFormat.name)

private fun logJournalpostErrors(errors: List<SafError>) {
    val msg = errors
            .map { err ->
                err.message +
                        " Lokasjon: " + err.locations.toString()
            }.reduce { a, b -> "$a \n $b" }
    LOG.error("Feil i kall mot SAF - dokumentoversiktBruker \n Mottat feilmelding: $msg")
}

private fun håndterJournalpostFeilKoder(statuskode: Int) {
    when (statuskode) {
        404 -> LOG.error("Responskode 404 fra SAF - dokumentoversiktBruker")
        500 -> LOG.warn("Responskode 500 fra SAF - dokumentoversiktBruker")
        else -> LOG.error("Ukjent feil i kall mot SAF - dokumentoversiktBruker. Statuskode: $statuskode")
    }
}

private fun håndterDokumentFeilKoder(statuskode: Int): TjenesteResultatWrapper {
    when (statuskode) {
        400 -> LOG.warn("Feil i SAF hentDokument. Ugyldig input. JournalpostId og dokumentInfoId må være tall og variantFormat må være en gyldig kodeverk-verdi")
        401 -> LOG.warn("Feil i SAF hentDokument. Bruker mangler tilgang for å vise dokumentet. Ugyldig OIDC token.")
        404 -> LOG.warn("Feil i SAF hentDokument. Dokument eller journalpost ble ikke funnet.")
    }
    return TjenesteResultatWrapper(null, fromStatusCode(statuskode))
}

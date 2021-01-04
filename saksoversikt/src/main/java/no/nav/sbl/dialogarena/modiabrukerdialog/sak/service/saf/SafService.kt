package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.saf

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.SubjectHandler
import no.nav.common.rest.client.RestClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Baksystem
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Dokument
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.DokumentMetadata
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere.ResultatWrapper
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere.TjenesteResultatWrapper
import okhttp3.*
import org.slf4j.LoggerFactory

val SAF_GRAPHQL_BASEURL: String = EnvironmentUtils.getRequiredProperty("SAF_GRAPHQL_URL")
val SAF_HENTDOKUMENT_BASEURL: String = EnvironmentUtils.getRequiredProperty("SAF_HENTDOKUMENT_URL")

private val LOG = LoggerFactory.getLogger(SafService::class.java)
private val mapper: ObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())

class SafService {
    private val jsonType: MediaType? = MediaType.parse("application/json; charset=utf-8")
    private val client: OkHttpClient = RestClient.baseClient()
    fun hentJournalposter(fnr: String): ResultatWrapper<List<DokumentMetadata>> {
        val jsonQuery = dokumentoversiktBrukerJsonQuery(fnr)
        val response = client.newCall(
                veilederAutorisertClient(SAF_GRAPHQL_BASEURL)
                        .post(RequestBody.create(jsonType, jsonQuery))
                        .build()
        ).execute()

        return handterStatus(response)
    }

    fun hentDokument(journalpostId: String, dokumentInfoId: String, variantFormat: Dokument.Variantformat): TjenesteResultatWrapper {
        val url = lagHentDokumentURL(journalpostId, dokumentInfoId, variantFormat)

        val response = client.newCall(
                veilederAutorisertClient(url).build()
        ).execute()
        return when (response.code()) {
            200 -> TjenesteResultatWrapper(response.body()?.bytes())
            else -> handterDokumentFeilKoder(response.code())
        }
    }
}

private fun handterStatus(response: Response): ResultatWrapper<List<DokumentMetadata>> =
        when (response.code()) {
            200 -> handterResponse(response)
            else -> {
                handterJournalpostFeilKoder(response.code())
                ResultatWrapper(emptyList(), setOf(Baksystem.SAF))
            }
        }

private fun handterResponse(response: Response): ResultatWrapper<List<DokumentMetadata>> {
    val safDokumentResponse = safDokumentResponsFraResponse(response)

    safDokumentResponse.errors?.also { logJournalpostErrors(safDokumentResponse.errors) }

    return ResultatWrapper(
            getDokumentMetadata(safDokumentResponse),
            safDokumentResponse.errors?.let { setOf(Baksystem.SAF) }.orEmpty()
    )
}

private fun safDokumentResponsFraResponse(response: Response): SafDokumentResponse {
    val rawJson = response.body()?.string()!!

    return try {
        mapper.readValue(rawJson)
    } catch (e: Exception) {
        throw RuntimeException("Feil i mapping for SAF query hentJournalposter, sjekk om query og objekt er like", e)
    }
}

private fun getDokumentMetadata(safDokumentResponse: SafDokumentResponse): List<DokumentMetadata> =
        safDokumentResponse.data?.dokumentoversiktBruker?.journalposter
                .orEmpty()
                .map { journalpost -> DokumentMetadata().fraSafJournalpost(journalpost) }

private fun veilederAutorisertClient(url: String): Request.Builder {
    val veilederOidcToken = SubjectHandler.getSsoToken(SsoToken.Type.OIDC)
            .orElseThrow { IllegalStateException("Fant ikke OIDC-token") }

    return Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer $veilederOidcToken")
                    .header("Content-Type", "application/json")
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

private fun handterJournalpostFeilKoder(statuskode: Int) {
    when (statuskode) {
        404 -> LOG.error("Responskode 404 fra SAF - dokumentoversiktBruker")
        500 -> LOG.warn("Responskode 500 fra SAF - dokumentoversiktBruker")
        else -> LOG.error("Ukjent feil i kall mot SAF - dokumentoversiktBruker. Statuskode: $statuskode")
    }
}

private fun handterDokumentFeilKoder(statuskode: Int): TjenesteResultatWrapper {
    when (statuskode) {
        400 -> LOG.warn("Feil i SAF hentDokument. Ugyldig input. JournalpostId og dokumentInfoId må være tall og variantFormat må være en gyldig kodeverk-verdi")
        401 -> LOG.warn("Feil i SAF hentDokument. Bruker mangler tilgang for å vise dokumentet. Ugyldig OIDC token.")
        404 -> LOG.warn("Feil i SAF hentDokument. Dokument eller journalpost ble ikke funnet.")
    }
    return TjenesteResultatWrapper(null, statuskode)
}

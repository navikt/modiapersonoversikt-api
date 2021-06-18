package no.nav.modiapersonoversikt.legacy.sak.service.saf

import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.SubjectHandler
import no.nav.common.log.MDCConstants
import no.nav.common.rest.client.RestClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.infrastructure.http.*
import no.nav.modiapersonoversikt.legacy.api.domain.saf.generated.HentBrukersDokumenter
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Baksystem
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Dokument
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.DokumentMetadata
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.resultatwrappere.ResultatWrapper
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.resultatwrappere.TjenesteResultatWrapper
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.net.URL
import java.util.*

@KtorExperimentalAPI
class SafGraphqlServiceImpl : SafService {
    private val LOG = LoggerFactory.getLogger(SafService::class.java)
    private val SAF_GRAPHQL_BASEURL: String = EnvironmentUtils.getRequiredProperty("SAF_GRAPHQL_URL")
    private val SAF_HENTDOKUMENT_BASEURL: String = EnvironmentUtils.getRequiredProperty("SAF_HENTDOKUMENT_URL")

    private val graphQLClient = LoggingGraphqlClient("SAF", URL(SAF_GRAPHQL_BASEURL))

    private val client: OkHttpClient = RestClient.baseClient().newBuilder()
        .addInterceptor(
            HeadersInterceptor {
                val token = SubjectHandler.getSsoToken(SsoToken.Type.OIDC)
                    .orElseThrow { IllegalStateException("Fant ikke OIDC-token") }
                val callId = MDC.get(MDCConstants.MDC_CALL_ID) ?: UUID.randomUUID().toString()
                mapOf(
                    "Authorization" to "Bearer $token",
                    "Content-Type" to "application/json",
                    "X-Correlation-ID" to callId
                )
            }
        )
        .addInterceptor(
            LoggingInterceptor("Saf") { request ->
                requireNotNull(request.header("X-Correlation-ID")) {
                    "Kall uten \"X-Correlation-ID\" er ikke lov"
                }
            }
        )
        .build()

    override fun hentJournalposter(fnr: String): ResultatWrapper<List<DokumentMetadata>> {
        val variables = HentBrukersDokumenter.Variables(
            HentBrukersDokumenter.BrukerIdInput(
                id = fnr,
                type = HentBrukersDokumenter.BrukerIdType.FNR
            )
        )

        return runBlocking {
            val response = HentBrukersDokumenter(graphQLClient).execute(variables)
            if (response.errors.isNullOrEmpty()) {
                val data = requireNotNull(response.data)
                    .dokumentoversiktBruker
                    .journalposter
                    .filterNotNull()
                    .map { it.toDokumentMetadata() }
                ResultatWrapper(data, emptySet())
            } else {
                ResultatWrapper(emptyList(), setOf(Baksystem.SAF))
            }
        }
    }

    override fun hentDokument(
        journalpostId: String,
        dokumentInfoId: String,
        variantFormat: Dokument.Variantformat
    ): TjenesteResultatWrapper {
        val url = "$SAF_HENTDOKUMENT_BASEURL/$journalpostId/$dokumentInfoId/${variantFormat.name}"
        val response = client.newCall(
            Request.Builder().url(url).build()
        ).execute()
        return when (response.code()) {
            200 -> TjenesteResultatWrapper(response.body()?.bytes())
            else -> handterDokumentFeilKoder(response.code())
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

    private fun HentBrukersDokumenter.Journalpost.toDokumentMetadata(): DokumentMetadata {
        val dokument = DokumentMetadata()

        // TODO Mapping fra graphql til DokumentMetadata, tilsvarende hva man finner i SafDokumentMapper

        return dokument
    }
}

package no.nav.modiapersonoversikt.legacy.sak.service.saf

import com.expediagroup.graphql.types.GraphQLResponse
import io.ktor.client.request.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import no.nav.common.rest.client.RestClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.http.*
import no.nav.modiapersonoversikt.legacy.api.domain.saf.generated.HentBrukersDokumenter
import no.nav.modiapersonoversikt.legacy.api.domain.saf.generated.HentBrukersDokumenter.Journalposttype
import no.nav.modiapersonoversikt.legacy.api.domain.saf.generated.Hentbrukerssaker
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Baksystem
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Dokument
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.DokumentMetadata
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.resultatwrappere.ResultatWrapper
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.resultatwrappere.TjenesteResultatWrapper
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.LoggerFactory
import java.net.URL

interface SafService {
    fun hentJournalposter(fnr: String): ResultatWrapper<List<DokumentMetadata>>
    fun hentDokument(
        journalpostId: String,
        dokumentInfoId: String,
        variantFormat: Dokument.Variantformat
    ): TjenesteResultatWrapper

    fun hentSaker(ident: String): GraphQLResponse<Hentbrukerssaker.Result>
}

class SafServiceImpl : SafService {
    private val LOG = LoggerFactory.getLogger(SafService::class.java)
    private val client: OkHttpClient = RestClient.baseClient().newBuilder()
        .addInterceptor(HeadersInterceptor(this::httpHeaders))
        .addInterceptor(
            LoggingInterceptor("Saf") { request ->
                requireNotNull(request.header("X-Correlation-ID")) {
                    "Kall uten \"X-Correlation-ID\" er ikke lov"
                }
            }
        )
        .build()

    companion object {
        private val SAF_GRAPHQL_BASEURL: String = EnvironmentUtils.getRequiredProperty("SAF_GRAPHQL_URL")
        private val SAF_HENTDOKUMENT_BASEURL: String = EnvironmentUtils.getRequiredProperty("SAF_HENTDOKUMENT_URL")

        @KtorExperimentalAPI
        private val graphQLClient = LoggingGraphqlClient("SAF", URL(SAF_GRAPHQL_BASEURL))

        const val VEDLEGG_START_INDEX = 1

        val JOURNALPOSTTYPE_INN = Journalposttype.I
        val JOURNALPOSTTYPE_UT = Journalposttype.U
        val JOURNALPOSTTYPE_INTERN = Journalposttype.N
    }

    override fun hentJournalposter(fnr: String): ResultatWrapper<List<DokumentMetadata>> {
        val variables = HentBrukersDokumenter.Variables(
            HentBrukersDokumenter.BrukerIdInput(
                id = fnr,
                type = HentBrukersDokumenter.BrukerIdType.FNR
            )
        )

        return runBlocking {
            val response = HentBrukersDokumenter(graphQLClient).execute(variables, userTokenAuthorizationHeaders)
            if (response.errors.isNullOrEmpty()) {
                val data = requireNotNull(response.data)
                    .dokumentoversiktBruker
                    .journalposter
                    .filterNotNull()
                    .map { DokumentMetadata().fraSafJournalpost(it) }
                ResultatWrapper(data, emptySet())
            } else {
                ResultatWrapper(emptyList(), setOf(Baksystem.SAF))
            }
        }
    }

    override fun hentSaker(ident: String): GraphQLResponse<Hentbrukerssaker.Result> {
        val variables = if (ident.length == 11) {
            Hentbrukerssaker.Variables(
                Hentbrukerssaker.BrukerIdInput(
                    id = ident,
                    type = Hentbrukerssaker.BrukerIdType.FNR
                )
            )
        } else {
            Hentbrukerssaker.Variables(
                Hentbrukerssaker.BrukerIdInput(
                    id = ident,
                    type = Hentbrukerssaker.BrukerIdType.AKTOERID
                )
            )
        }
        return runBlocking {
            Hentbrukerssaker(graphQLClient)
                .execute(variables, userTokenAuthorizationHeaders)
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

    private fun httpHeaders(): Map<String, String> {
        val token = AuthContextUtils.requireToken()
        val callId = getCallId()
        return mapOf(
            "Authorization" to "Bearer $token",
            "Content-Type" to "application/json",
            "X-Correlation-ID" to callId
        )
    }

    private val userTokenAuthorizationHeaders: HeadersBuilder = {
        httpHeaders().forEach { (key, value) -> header(key, value) }
    }

    private fun handterDokumentFeilKoder(statuskode: Int): TjenesteResultatWrapper {
        when (statuskode) {
            400 -> LOG.warn("Feil i SAF hentDokument. Ugyldig input. JournalpostId og dokumentInfoId må være tall og variantFormat må være en gyldig kodeverk-verdi")
            401 -> LOG.warn("Feil i SAF hentDokument. Bruker mangler tilgang for å vise dokumentet. Ugyldig OIDC token.")
            404 -> LOG.warn("Feil i SAF hentDokument. Dokument eller journalpost ble ikke funnet.")
        }
        return TjenesteResultatWrapper(null, statuskode)
    }
}

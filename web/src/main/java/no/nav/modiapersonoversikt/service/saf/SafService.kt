package no.nav.modiapersonoversikt.service.saf

import com.expediagroup.graphql.client.types.GraphQLClientResponse
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import no.nav.common.rest.client.RestClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.commondomain.sak.Baksystem
import no.nav.modiapersonoversikt.commondomain.sak.ResultatWrapper
import no.nav.modiapersonoversikt.commondomain.sak.TjenesteResultatWrapper
import no.nav.modiapersonoversikt.consumer.saf.generated.HentBrukersDokumenter
import no.nav.modiapersonoversikt.consumer.saf.generated.HentBrukersSaker
import no.nav.modiapersonoversikt.consumer.saf.generated.enums.BrukerIdType
import no.nav.modiapersonoversikt.consumer.saf.generated.enums.Journalposttype
import no.nav.modiapersonoversikt.consumer.saf.generated.inputs.BrukerIdInput
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.http.*
import no.nav.modiapersonoversikt.service.saf.SafDokumentMapper.fraSafJournalpost
import no.nav.modiapersonoversikt.service.saf.domain.Dokument
import no.nav.modiapersonoversikt.service.saf.domain.DokumentMetadata
import no.nav.modiapersonoversikt.utils.BoundedOnBehalfOfTokenClient
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.LoggerFactory
import java.net.URL

interface SafService {
    fun hentJournalposter(fnr: String): ResultatWrapper<List<DokumentMetadata>>

    fun hentDokument(
        journalpostId: String,
        dokumentInfoId: String,
        variantFormat: Dokument.Variantformat,
    ): TjenesteResultatWrapper

    fun hentSaker(ident: String): GraphQLClientResponse<HentBrukersSaker.Result>
}

private val SAF_GRAPHQL_BASEURL: String = EnvironmentUtils.getRequiredProperty("SAF_GRAPHQL_URL")
private val SAF_HENTDOKUMENT_BASEURL: String = EnvironmentUtils.getRequiredProperty("SAF_HENTDOKUMENT_URL")

private val graphQLClient = LoggingGraphqlClient("SAF", URL(SAF_GRAPHQL_BASEURL))

class SafServiceImpl(
    private val oboTokenProvider: BoundedOnBehalfOfTokenClient,
) : SafService {
    private val log = LoggerFactory.getLogger(SafService::class.java)
    private val client: OkHttpClient =
        RestClient.baseClient().newBuilder()
            .addInterceptor(
                HeadersInterceptor {
                    this.httpHeaders(oboTokenProvider)
                },
            )
            .addInterceptor(
                LoggingInterceptor("Saf") { request ->
                    requireNotNull(request.header("X-Correlation-ID")) {
                        "Kall uten \"X-Correlation-ID\" er ikke lov"
                    }
                },
            )
            .build()

    companion object {
        const val VEDLEGG_START_INDEX = 1

        val JOURNALPOSTTYPE_INN = Journalposttype.I
        val JOURNALPOSTTYPE_UT = Journalposttype.U
        val JOURNALPOSTTYPE_INTERN = Journalposttype.N
    }

    override fun hentJournalposter(fnr: String): ResultatWrapper<List<DokumentMetadata>> {
        val variables =
            HentBrukersDokumenter.Variables(
                BrukerIdInput(
                    id = fnr,
                    type = BrukerIdType.FNR,
                ),
            )

        return runBlocking {
            val response = graphQLClient.execute(HentBrukersDokumenter(variables), userTokenAuthorizationHeaders)
            if (response.errors.isNullOrEmpty()) {
                val data =
                    requireNotNull(response.data)
                        .dokumentoversiktBruker
                        .journalposter
                        .filterNotNull()
                        .mapNotNull { fraSafJournalpost(it) }
                ResultatWrapper(
                    data,
                    emptySet(),
                )
            } else {
                ResultatWrapper(
                    emptyList(),
                    setOf(Baksystem.SAF),
                )
            }
        }
    }

    override fun hentSaker(ident: String): GraphQLClientResponse<HentBrukersSaker.Result> {
        val variables =
            if (ident.length == 11) {
                HentBrukersSaker.Variables(
                    BrukerIdInput(
                        id = ident,
                        type = BrukerIdType.FNR,
                    ),
                )
            } else {
                HentBrukersSaker.Variables(
                    BrukerIdInput(
                        id = ident,
                        type = BrukerIdType.AKTOERID,
                    ),
                )
            }
        return runBlocking {
            graphQLClient
                .execute(HentBrukersSaker(variables), userTokenAuthorizationHeaders)
        }
    }

    override fun hentDokument(
        journalpostId: String,
        dokumentInfoId: String,
        variantFormat: Dokument.Variantformat,
    ): TjenesteResultatWrapper {
        val url = "$SAF_HENTDOKUMENT_BASEURL/$journalpostId/$dokumentInfoId/${variantFormat.name}"
        val response =
            client.newCall(
                Request.Builder().url(url).build(),
            ).execute()
        return when (response.code) {
            200 ->
                TjenesteResultatWrapper(
                    response.body?.bytes(),
                )
            else -> handterDokumentFeilKoder(response.code)
        }
    }

    private fun httpHeaders(oboTokenProvider: BoundedOnBehalfOfTokenClient): Map<String, String> {
        val token = AuthContextUtils.requireBoundedClientOboToken(oboTokenProvider)
        val callId = getCallId()
        return mapOf(
            "Authorization" to "Bearer $token",
            "Content-Type" to "application/json",
            "X-Correlation-ID" to callId,
        )
    }

    private val userTokenAuthorizationHeaders: HeadersBuilder = {
        httpHeaders(oboTokenProvider).forEach { (key, value) -> header(key, value) }
    }

    private fun handterDokumentFeilKoder(statuskode: Int): TjenesteResultatWrapper {
        when (statuskode) {
            400 ->
                log.warn(
                    """
                    Feil i SAF hentDokument. Ugyldig input. JournalpostId og dokumentInfoId må være tall og variantFormat må være en gyldig kodeverk-verdi
                    """.trim(),
                )
            401 -> log.warn("Feil i SAF hentDokument. Bruker mangler tilgang for å vise dokumentet. Ugyldig OIDC token.")
            404 -> log.warn("Feil i SAF hentDokument. Dokument eller journalpost ble ikke funnet.")
        }
        return TjenesteResultatWrapper(
            null,
            statuskode,
        )
    }
}

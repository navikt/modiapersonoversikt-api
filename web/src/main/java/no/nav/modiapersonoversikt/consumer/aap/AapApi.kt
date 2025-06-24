package no.nav.modiapersonoversikt.consumer.aap

import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import no.nav.modiapersonoversikt.infrastructure.http.OkHttpUtils
import no.nav.modiapersonoversikt.service.journalforingsaker.JournalforingSak
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

interface AapApi {
    fun hentAapSaker(fnr: String): List<JournalforingSak>
}

@Serializable
data class SakerRequest(
    val personidentifikatorer: List<String>,
)

data class AapSaker(
    val kilde: String,
    val periode: SakPeriode,
    val sakId: String,
    val statusKode: String,
) {
    fun toJournalforingSak(): JournalforingSak =
        JournalforingSak().apply {
            saksId = sakId
            fagsystemSaksId = saksId
            temaKode = "AAP"
            fagsystemKode = kilde
            sakstype = "GEN"
        }
}

@Serializable
data class SakPeriode(
    val fraOgMedDato: String,
    val tilOgMedDato: String,
)

open class AapApiImpl(
    private val baseUrl: String,
    private val httpClient: OkHttpClient,
) : AapApi {
    override fun hentAapSaker(fnr: String): List<JournalforingSak> {
        val requestContent = Json.encodeToString(SakerRequest(personidentifikatorer = listOf(fnr)))
        val aapSaker: List<AapSaker> = sendRequest("sakerByFnr", requestContent) ?: listOf()
        return aapSaker.map { it.toJournalforingSak() }
    }

    private inline fun <reified T> sendRequest(
        url: String,
        requestContent: String,
    ): T? {
        val requestBody =
            requestContent
                .toRequestBody("application/json".toMediaTypeOrNull())

        val response =
            httpClient
                .newCall(
                    Request
                        .Builder()
                        .url("$baseUrl/$url")
                        .post(requestBody)
                        .build(),
                ).execute()

        if (response.body?.contentLength() == 0L) return null

        return response.body?.let { OkHttpUtils.objectMapper.readValue(it.string()) }
    }
}

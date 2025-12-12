package no.nav.modiapersonoversikt.consumer.pensjon

import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import no.nav.modiapersonoversikt.infrastructure.http.OkHttpUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.cache.annotation.Cacheable
import java.time.LocalDate

interface PensjonService {
    fun hentSaker(fnr: String): List<PensjonSak>
}

data class PensjonSakResponse(
    val sakSammendragListe: List<PensjonSak>,
)

data class PensjonSak(
    val sakid: Long,
    val sakType: String,
    val sakStatus: String,
    val fomDato: LocalDate?,
    val tomDato: LocalDate?,
    val enhetsId: String,
)

@Serializable
data class RequestBodyContent(
    val fnr: String,
)

open class PensjonServiceImpl(
    private val baseUrl: String,
    private val httpClient: OkHttpClient,
) : PensjonService {
    @Cacheable("pensjonSaker")
    override fun hentSaker(fnr: String): List<PensjonSak> {
        val response: PensjonSakResponse? = sendRequest<PensjonSakResponse>(fnr)
        return response?.sakSammendragListe ?: listOf()
    }

    private inline fun <reified T> sendRequest(fnr: String): T? {
        val requestContent =
            Json.encodeToString(
                RequestBodyContent(
                    fnr,
                ),
            )
        val requestBody =
            requestContent
                .toRequestBody("application/json".toMediaTypeOrNull())

        val response =
            httpClient
                .newCall(
                    Request
                        .Builder()
                        .url("$baseUrl/pen/api/ekstern/modia/sak/sammendrag")
                        .post(requestBody)
                        .build(),
                ).execute()

        if (response.body?.contentLength() == 0L) return null

        return response.body?.let { OkHttpUtils.objectMapper.readValue(it.string()) }
    }
}

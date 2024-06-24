package no.nav.modiapersonoversikt.consumer.arenainfotrygdproxy

import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import no.nav.modiapersonoversikt.consumer.arenainfotrygdproxy.domain.OppfolgingskontraktResponse
import no.nav.modiapersonoversikt.consumer.arenainfotrygdproxy.domain.YtelseskontraktResponse
import no.nav.modiapersonoversikt.infrastructure.http.OkHttpUtils
import no.nav.modiapersonoversikt.service.journalforingsaker.JournalforingSak
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.cache.annotation.Cacheable

interface ArenaInfotrygdApi {
    fun hentYtelseskontrakter(
        fnr: String,
        start: String?,
        slutt: String?,
    ): YtelseskontraktResponse

    fun hentOppfolgingskontrakter(
        fnr: String,
        start: String?,
        slutt: String?,
    ): OppfolgingskontraktResponse

    fun hentSykepenger(fnr: String): Map<String, Any?>

    fun hentForeldrepenger(fnr: String): Map<String, Any?>

    fun hentPleiepenger(fnr: String): Map<String, Any?>

    fun hentOppfolgingssakFraArena(fnr: String): JournalforingSak?
}

@Serializable
data class RequestBodyContent(
    val fnr: String,
    val start: String?,
    val slutt: String?,
)

open class ArenaInfotrygdApiImpl(
    private val baseUrl: String,
    private val httpClient: OkHttpClient,
) : ArenaInfotrygdApi {
    @Cacheable(value = ["ytelseskontrakterCache"])
    override fun hentYtelseskontrakter(
        fnr: String,
        start: String?,
        slutt: String?,
    ): YtelseskontraktResponse {
        val json = Json.encodeToString(RequestBodyContent(fnr, start, slutt))
        return sendRequest("ytelseskontrakter", json)
    }

    @Cacheable(value = ["OppfolgingskontrakterCache"])
    override fun hentOppfolgingskontrakter(
        fnr: String,
        start: String?,
        slutt: String?,
    ): OppfolgingskontraktResponse {
        val json = Json.encodeToString(RequestBodyContent(fnr, start, slutt))
        return sendRequest("Oppfolgingskontrakter", json)
    }

    @Cacheable(value = ["oppfolgingssakFraArenaCache"])
    override fun hentOppfolgingssakFraArena(fnr: String): JournalforingSak? {
        return sendRequest("sakvedtak", fnr)
    }

    @Cacheable(value = ["sykePengerCache"])
    override fun hentSykepenger(fnr: String): Map<String, Any?> {
        return sendRequest("sykepenger", fnr)
    }

    @Cacheable(value = ["foreldrePengerCache"])
    override fun hentForeldrepenger(fnr: String): Map<String, Any?> {
        return sendRequest("foreldrepenger", fnr)
    }

    @Cacheable(value = ["pleiePengerCache"])
    override fun hentPleiepenger(fnr: String): Map<String, Any?> {
        return sendRequest("pleiepenger", fnr)
    }

    private inline fun <reified T> sendRequest(
        url: String,
        json: String,
    ): T {
        val body =
            json
                .toRequestBody("application/json".toMediaTypeOrNull())
        val response =
            httpClient
                .newCall(
                    Request
                        .Builder()
                        .url("$baseUrl/rest/$url")
                        .post(body)
                        .build(),
                )
                .execute()

        val bodyContent = checkNotNull(response.body?.string()) { "No Content" }

        return OkHttpUtils.objectMapper.readValue(bodyContent)
    }
}
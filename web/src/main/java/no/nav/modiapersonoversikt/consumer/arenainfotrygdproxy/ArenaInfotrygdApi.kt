package no.nav.modiapersonoversikt.consumer.arenainfotrygdproxy

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.modiapersonoversikt.consumer.arenainfotrygdproxy.domain.OppfolgingskontraktResponse
import no.nav.modiapersonoversikt.consumer.arenainfotrygdproxy.domain.YtelseskontraktResponse
import no.nav.modiapersonoversikt.infrastructure.http.OkHttpUtils
import no.nav.modiapersonoversikt.service.journalforingsaker.JournalforingSak
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

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

class ArenaInfotrygdApiImpl(
    private val baseUrl: String,
    private val httpClient: OkHttpClient,
) : ArenaInfotrygdApi {
    override fun hentYtelseskontrakter(
        fnr: String,
        start: String?,
        slutt: String?,
    ): YtelseskontraktResponse {
        val builder = FormBody.Builder()
        builder.add("fnr", fnr)
        if (start != null) {
            builder.add("start", start)
        }
        if (slutt != null) {
            builder.add("slutt", slutt)
        }
        val formBody = builder.build()
        val response =
            httpClient
                .newCall(
                    Request
                        .Builder()
                        .post(formBody)
                        .url("$baseUrl/rest/ytelseskontrakter")
                        .build(),
                )
                .execute()

        val bodyContent = checkNotNull(response.body?.string()) { "No Content" }

        return OkHttpUtils.objectMapper.readValue(bodyContent)
    }

    override fun hentOppfolgingskontrakter(
        fnr: String,
        start: String?,
        slutt: String?,
    ): OppfolgingskontraktResponse {
        val builder = FormBody.Builder()
        builder.add("fnr", fnr)
        if (start != null) {
            builder.add("start", start)
        }
        if (slutt != null) {
            builder.add("slutt", slutt)
        }
        val formBody = builder.build()
        val response =
            httpClient
                .newCall(
                    Request
                        .Builder()
                        .post(formBody)
                        .url("$baseUrl/rest/Oppfolgingskontrakter")
                        .build(),
                )
                .execute()

        val bodyContent = checkNotNull(response.body?.string()) { "No Content" }
        return OkHttpUtils.objectMapper.readValue(bodyContent)
    }

    override fun hentOppfolgingssakFraArena(fnr: String): JournalforingSak? {
        val builder = FormBody.Builder()
        builder.add("fnr", fnr)
        val formBody = builder.build()
        val response =
            httpClient
                .newCall(
                    Request
                        .Builder()
                        .post(formBody)
                        .url("$baseUrl/rest/sakvedtak")
                        .build(),
                )
                .execute()

        val bodyContent = checkNotNull(response.body?.string()) { "No Content" }
        return OkHttpUtils.objectMapper.readValue(bodyContent)
    }

    override fun hentSykepenger(fnr: String): Map<String, Any?> {
        val builder = FormBody.Builder()
        builder.add("fnr", fnr)
        val formBody = builder.build()
        val response =
            httpClient
                .newCall(
                    Request
                        .Builder()
                        .post(formBody)
                        .url("$baseUrl/rest/sykepenger")
                        .build(),
                )
                .execute()

        val bodyContent = checkNotNull(response.body?.string()) { "No Content" }
        return OkHttpUtils.objectMapper.readValue(bodyContent)
    }

    override fun hentForeldrepenger(fnr: String): Map<String, Any?> {
        val builder = FormBody.Builder()
        builder.add("fnr", fnr)
        val formBody = builder.build()
        val response =
            httpClient
                .newCall(
                    Request
                        .Builder()
                        .post(formBody)
                        .url("$baseUrl/rest/foreldrepenger")
                        .build(),
                )
                .execute()

        val bodyContent = checkNotNull(response.body?.string()) { "No Content" }
        return OkHttpUtils.objectMapper.readValue(bodyContent)
    }

    override fun hentPleiepenger(fnr: String): Map<String, Any?> {
        val builder = FormBody.Builder()
        builder.add("fnr", fnr)
        val formBody = builder.build()
        val response =
            httpClient
                .newCall(
                    Request
                        .Builder()
                        .post(formBody)
                        .url("$baseUrl/rest/pleiepenger")
                        .build(),
                )
                .execute()

        val bodyContent = checkNotNull(response.body?.string()) { "No Content" }
        return OkHttpUtils.objectMapper.readValue(bodyContent)
    }
}

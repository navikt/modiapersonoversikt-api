package no.nav.modiapersonoversikt.consumer.pensjon

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.modiapersonoversikt.infrastructure.http.OkHttpUtils
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.cache.annotation.Cacheable
import java.time.LocalDate

interface PensjonService {
    fun hentSaker(fnr: String): List<PensjonSak>

    fun hentVedtak(
        fnr: String,
        sakId: String,
    ): List<PensjonVedtak>
}

data class PensjonSak(
    val id: Long,
    val type: Code?,
    val status: Code?,
    val fom: LocalDate?,
    val tom: LocalDate?,
    val enhetId: String,
)

data class PensjonVedtak(
    val id: Long,
    val type: Code?,
    val status: Code?,
    val vedtaksdato: LocalDate?,
    val lopendeFom: LocalDate?,
    val lopendeTom: LocalDate?,
)

data class Code(
    val code: String,
    val decode: String,
)

open class PensjonServiceImpl(
    private val baseUrl: String,
    private val httpClient: OkHttpClient,
) : PensjonService {
    @Cacheable("pensjonSaker")
    override fun hentSaker(fnr: String): List<PensjonSak> = sendRequest("sak", fnr) ?: listOf()

    @Cacheable("pensjonVedtak")
    override fun hentVedtak(
        fnr: String,
        sakId: String,
    ): List<PensjonVedtak> = sendRequest("/sak/$sakId/vedtak", fnr) ?: listOf()

    private inline fun <reified T> sendRequest(
        url: String,
        fnr: String,
    ): T? {
        val response =
            httpClient
                .newCall(
                    Request
                        .Builder()
                        .url("$baseUrl/rest/api/$url")
                        .addHeader("fnr", fnr)
                        .get()
                        .build(),
                ).execute()

        if (response.body?.contentLength() == 0L) return null

        return response.body?.let { OkHttpUtils.objectMapper.readValue(it.string()) }
    }
}

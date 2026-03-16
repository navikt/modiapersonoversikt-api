package no.nav.modiapersonoversikt.consumer.dagpenger

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.models.DatadelingRequestDagpengerDto
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.models.DatadelingResponseDagpengerDto
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.models.PeriodeDagpengerDto
import no.nav.modiapersonoversikt.infrastructure.http.OkHttpUtils
import no.nav.modiapersonoversikt.infrastructure.http.OkHttpUtils.objectMapper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDate

/**
 * Not actually a vedtak, but rather a collection of periods in which payment is
 * granted. Treated as a vedtak by our frontend for more compact display. To be
 * replaced if/when the /vedtak end point is activated.
 */
data class PseudoDagpengerVedtak(
    val perioder: List<PeriodeDagpengerDto>,
) {
    val nyesteFraOgMedDato: LocalDate? get() =
        perioder
            .map {
                it.fraOgMedDato
            }.sortedDescending()
            .firstOrNull()
}

interface DagpengerService {
    fun hentVedtak(datodelingRequest: DatadelingRequestDagpengerDto): PseudoDagpengerVedtak
}

open class DagpengerServiceImpl(
    val baseUrl: String,
    val client: OkHttpClient,
) : DagpengerService {
    override fun hentVedtak(datodelingRequest: DatadelingRequestDagpengerDto): PseudoDagpengerVedtak {
        val url = "$baseUrl/dagpenger/datadeling/v1/perioder"
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        val requestContent =
            mapper.writeValueAsString(
                datodelingRequest,
            )
        val requestBody =
            requestContent
                .toRequestBody("application/json".toMediaTypeOrNull())
        val response =
            client
                .newCall(
                    Request
                        .Builder()
                        .url(url)
                        .post(requestBody)
                        .build(),
                ).execute()

        // TODO throw exception here maybe
        if (response.body?.contentLength() == 0L) return PseudoDagpengerVedtak(listOf())
        return response.body?.let {
            PseudoDagpengerVedtak((OkHttpUtils.objectMapper.readValue(it.string()) as DatadelingResponseDagpengerDto).perioder)
        } ?: PseudoDagpengerVedtak(listOf())
    }
}

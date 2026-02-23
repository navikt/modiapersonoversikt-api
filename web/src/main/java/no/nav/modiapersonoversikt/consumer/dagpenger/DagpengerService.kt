package no.nav.modiapersonoversikt.consumer.dagpenger

import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.models.DatadelingResponseDagpengerDto
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.models.PeriodeDagpengerDto
import no.nav.modiapersonoversikt.infrastructure.http.OkHttpUtils
import no.nav.modiapersonoversikt.rest.common.FnrDatoRangeRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

interface DagpengerService {
    fun hentPerioder(fnrRequest: FnrDatoRangeRequest): List<PeriodeDagpengerDto>
}

@Serializable
data class RequestBodyContent( // just replace this one with DatadelingRequestDagpengerDto
    val personIdent: String,
    val fraOgMedDato: String,
    val tilOgMedDato: String? = null,
)

open class DagpengerServiceImpl(
    val baseUrl: String,
    val client: OkHttpClient,
) : DagpengerService {
    override fun hentPerioder(fnrRequest: FnrDatoRangeRequest): List<PeriodeDagpengerDto> {
        val url = "$baseUrl/dagpenger/datadeling/v1/perioder"
        val requestContent =
            Json.encodeToString(
                RequestBodyContent(
                    fnrRequest.fnr,
                    fnrRequest.fom!!, // TODO handle nicelier
                    fnrRequest.tom,
                ),
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

        if (response.body?.contentLength() == 0L) return listOf() // TODO throw exception here maybe
        return response.body?.let {
            (OkHttpUtils.objectMapper.readValue(it.string()) as DatadelingResponseDagpengerDto).perioder
        } ?: listOf()
        //listOf(
        //    PeriodeDagpengerDto(
        //        fraOgMedDato = LocalDate.of(2025, 6, 7),
        //        ytelseType = YtelseTypeDagpengerDto.decode("DAGPENGER_PERMITTERING_FISKEINDUSTRI")!!,
        //    ),
        //)
    }
}

package no.nav.modiapersonoversikt.consumer.dagpenger

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.models.DatadelingRequestDagpengerDto
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.models.DatadelingResponseDagpengerDto
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.models.PeriodeDagpengerDto
import no.nav.modiapersonoversikt.infrastructure.http.OkHttpUtils
import no.nav.modiapersonoversikt.infrastructure.http.OkHttpUtils.objectMapper
import no.nav.modiapersonoversikt.rest.common.FnrDatoRangeRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDate

interface DagpengerService {
    fun hentPerioder(fnrRequest: FnrDatoRangeRequest): List<PeriodeDagpengerDto>
}

open class DagpengerServiceImpl(
    val baseUrl: String,
    val client: OkHttpClient,
) : DagpengerService {
    override fun hentPerioder(fnrRequest: FnrDatoRangeRequest): List<PeriodeDagpengerDto> {
        val url = "$baseUrl/dagpenger/datadeling/v1/perioder"
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        val requestContent =
            mapper.writeValueAsString(
                DatadelingRequestDagpengerDto(
                    fnrRequest.fnr,
                    LocalDate.parse(fnrRequest.fom!!), // TODO handle nicelier
                    fnrRequest.tom?.let { LocalDate.parse(it) },
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

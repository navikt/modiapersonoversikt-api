package no.nav.modiapersonoversikt.consumer.spokelse

import no.nav.modiapersonoversikt.infrastructure.http.OkHttpUtils.objectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDate

data class SykepengerSpokelse(
    val utbetaltePerioder: List<Utbetalingsperiode>,
)

data class SykpengerRequest(
    val personidentifikatorer: List<String>,
    val fom: String?,
    val tom: String?,
)

data class Utbetalingsperiode(
    val fom: LocalDate,
    val tom: LocalDate,
    val grad: Double,
    val tags: Set<String> = emptySet(),
)

interface SpokelseClient {
    fun hentUtbetalingsperiode(
        fnr: String,
        fom: String?,
        tom: String?,
    ): SykepengerSpokelse
}

open class SpokelseClientImpl(
    private val baseUrl: String,
    private val httpClient: OkHttpClient,
) : SpokelseClient {
    override fun hentUtbetalingsperiode(
        fnr: String,
        fom: String?,
        tom: String?,
    ): SykepengerSpokelse {
        val requestBody =
            objectMapper
                .writeValueAsString(
                    SykpengerRequest(
                        personidentifikatorer = listOf(fnr),
                        fom = fom,
                        tom = tom,
                    ),
                ).toRequestBody("application/json".toMediaType())

        val response =
            httpClient
                .newCall(
                    Request
                        .Builder()
                        .url("$baseUrl/utbetalte-perioder-personoversikt")
                        .post(requestBody)
                        .build(),
                ).execute()

        val body = response.body?.string()

        val perioder = objectMapper.readValue(body, SykepengerSpokelse::class.java)

        return SykepengerSpokelse(
            utbetaltePerioder =
                perioder.utbetaltePerioder
                    // Infotrygd-perioder filtreres ut fordi de allerede hentes fra Infotrygd direkte via
                    // /rest/ytelse/sykepenger.
                    .filter { "Infotrygd" !in it.tags }
                    .sortedByDescending { it.fom },
        )
    }
}

package no.nav.modiapersonoversikt.consumer.spokelse

import kotlinx.datetime.LocalDateTime
import no.nav.modiapersonoversikt.infrastructure.http.OkHttpUtils.objectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDate

data class UtbetaltePerioder(
    val utbetaltePerioder: List<UtbetaltPeriode>,
)

data class SykepengerRequest(
    val personidentifikatorer: List<String>,
    val fom: LocalDate,
    val tom: LocalDate,
)

data class SykepengerVedtak(
    val vedtaksreferanse: String,
    val utbetalinger: List<UtbetaltPeriode>,
    val vedtattTidspunkt: LocalDateTime,
)

data class UtbetaltPeriode(
    val fom: LocalDate,
    val tom: LocalDate,
    val organisasjonsnummer: String, // probably
    val grad: Double,
)

interface SpokelseClient {
    fun hentUtbetaltePerioder(
        fnr: String,
        fom: LocalDate,
        tom: LocalDate,
    ): UtbetaltePerioder

    fun hentSykepengerVedtak(
        fnr: String,
        fom: LocalDate,
    ): List<SykepengerVedtak>
}

open class SpokelseClientImpl(
    private val baseUrl: String,
    private val httpClient: OkHttpClient,
) : SpokelseClient {
    override fun hentUtbetaltePerioder(
        fnr: String,
        fom: LocalDate,
        tom: LocalDate,
    ): UtbetaltePerioder {
        val requestBody =
            objectMapper
                .writeValueAsString(
                    SykepengerRequest(
                        personidentifikatorer = listOf(fnr),
                        fom = fom,
                        tom = tom,
                    ),
                ).toRequestBody("application/json".toMediaType())

        val body =
            httpClient
                .newCall(
                    Request
                        .Builder()
                        .url("$baseUrl/utbetalte-perioder-personoversikt")
                        .post(requestBody)
                        .build(),
                ).execute().body?.string()

        return objectMapper.readValue(body, UtbetaltePerioder::class.java)
    }

    override fun hentSykepengerVedtak(
        fnr: String,
        fom: LocalDate,
    ): List<SykepengerVedtak> {
        val body =
            httpClient
                .newCall(
                    Request
                        .Builder()
                        .url("$baseUrl/grunnlag?fodselsnummer=$fnr&fraDato=$fom")
                        .build(),
                ).execute().body?.string()

        return objectMapper.readValue(body, objectMapper.typeFactory.constructCollectionType(List::class.java, SykepengerVedtak::class.java))
    }
}

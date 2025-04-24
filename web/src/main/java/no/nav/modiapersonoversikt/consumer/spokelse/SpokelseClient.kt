package no.nav.modiapersonoversikt.consumer.spokelse

import kotlinx.datetime.LocalDateTime
import no.nav.modiapersonoversikt.infrastructure.http.OkHttpUtils.objectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDate

data class Utbetalingsperioder(
    val utbetaltePerioder: List<Utbetalingsperiode>,
)

data class SykpengerRequest(
    val personidentifikatorer: List<String>,
    val fom: LocalDate,
    val tom: LocalDate,
)

data class SykpengerVedtak(
    val vedtaksreferanse: String,
    val utbetalinger: List<Utbetalingsperiode>,
    val vedtattTidspunkt: LocalDateTime,
)

data class Utbetalingsperiode(
    val fom: LocalDate,
    val tom: LocalDate,
    val grad: Double,
)

interface SpokelseClient {
    fun hentUtbetalingsperiode(
        fnr: String,
        fom: LocalDate,
        tom: LocalDate,
    ): Utbetalingsperioder

    fun hentSykpengerVedtak(
        fnr: String,
        fom: LocalDate,
    ): List<SykpengerVedtak>
}

open class SpokelseClientImpl(
    private val baseUrl: String,
    private val httpClient: OkHttpClient,
) : SpokelseClient {
    override fun hentUtbetalingsperiode(
        fnr: String,
        fom: LocalDate,
        tom: LocalDate,
    ): Utbetalingsperioder {
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
                        .url("$baseUrl/utbetalte-perioder-dagpenger")
                        .post(requestBody)
                        .build(),
                ).execute()

        val body = response.body?.string()

        return objectMapper.readValue(body, Utbetalingsperioder::class.java)
    }

    override fun hentSykpengerVedtak(
        fnr: String,
        fom: LocalDate,
    ): List<SykpengerVedtak> {
        val response =
            httpClient
                .newCall(
                    Request
                        .Builder()
                        .url("$baseUrl/grunnlag?fodselsnummer=$fnr&fraDato=$fom")
                        .build(),
                ).execute()

        val body = response.body?.string()

        return objectMapper.readValue(body, objectMapper.typeFactory.constructCollectionType(List::class.java, SykpengerVedtak::class.java))
    }
}

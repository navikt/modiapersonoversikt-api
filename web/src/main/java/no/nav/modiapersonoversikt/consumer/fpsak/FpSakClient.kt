package no.nav.modiapersonoversikt.consumer.fpsak
import no.nav.modiapersonoversikt.infrastructure.http.OkHttpUtils.objectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.math.BigDecimal
import java.time.LocalDate

data class FpYtelse(
    val ytelse: FpYtelseType,
    val saksnummer: String,
    val utbetalinger: List<Utbetaling>,
    )



data class YtelserRequest(
    val ident: Ident,
    val fom: LocalDate,
    val tom: LocalDate?,
)

data class Ident(
    val verdi: String,
)

data class Periode(
    val fom: LocalDate,
    val tom: LocalDate?,
)

data class Utbetaling(
    val fom: LocalDate,
    val tom: LocalDate,
    val grad: BigDecimal,
)


enum class FpYtelseType {
    ENGANGSTØNAD,
    FORELDREPENGER,
    SVANGERSKAPSPENGER,
}


interface FpSakClient {
    fun hentYtelser(
        fnr: String,
        periode: Periode): List<FpYtelse>
}

open class FpSakClientImpl(
    private val url: String,
    private val httpClient: OkHttpClient,
) : FpSakClient {

    override fun hentYtelser(
        fnr: String,
        periode: Periode,
    ): List<FpYtelse> {
        val requestBody =
            objectMapper
                .writeValueAsString(
                    YtelserRequest(
                        ident = Ident(verdi = fnr),
                        fom = periode.fom,
                        tom = periode.tom,
                    ),
                ).toRequestBody("application/json".toMediaType())

        val response =
            httpClient
                .newCall(
                    Request
                        .Builder()
                        .url("$url/fpsak/ekstern/api/ytelseinfo/basis")
                        .post(requestBody)
                        .build(),
                ).execute()

        val body = response.body?.string()
        return objectMapper.readValue(body, objectMapper.typeFactory.constructCollectionType(List::class.java, FpYtelse::class.java))
    }
}
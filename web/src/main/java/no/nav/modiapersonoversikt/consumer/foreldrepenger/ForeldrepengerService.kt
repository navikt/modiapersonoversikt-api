package no.nav.modiapersonoversikt.consumer.foreldrepenger
import no.nav.modiapersonoversikt.infrastructure.http.OkHttpUtils.objectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.math.BigDecimal
import java.time.LocalDate

data class FpSakResponse(
    val ytelse: YtelseType,
    val saksnummer: String,
    val utbetalinger: List<Utbetaling>,
)

data class Utbetaling(
    val fom: LocalDate,
    val tom: LocalDate,
    val grad: BigDecimal,
)

data class ForeldrepengerPeriode(
    val fom: LocalDate,
    val tom: LocalDate,
    val grad: BigDecimal,
)

data class Foreldrepenger(
    val ytelse: YtelseType,
    val saksnummer: String,
    val perioder: List<ForeldrepengerPeriode>,
    val fom: LocalDate,
    val tom: LocalDate,
)

data class YtelserRequest(
    val ident: String,
    val fom: LocalDate?,
    val tom: LocalDate?,
)

enum class YtelseType {
    ENGANGSSTØNAD,
    FORELDREPENGER,
    SVANGERSKAPSPENGER,
}

interface ForeldrepengerService {
    fun hentForeldrepengerSortertPaaPeriode(
        fnr: String,
        fraOgMedDato: String?,
        tilOgMedDato: String?,
    ): List<Foreldrepenger>
}

open class ForeldrepengerServiceImpl(
    private val url: String,
    private val httpClient: OkHttpClient,
) : ForeldrepengerService {
    override fun hentForeldrepengerSortertPaaPeriode(
        fnr: String,
        fraOgMedDato: String?,
        tilOgMedDato: String?,
    ): List<Foreldrepenger> {
        val saker = hentYtelser(fnr, fraOgMedDato, tilOgMedDato)
        return mapFpSakResponse(saker)
    }

    internal fun mapFpSakResponse(sakResponse: List<FpSakResponse>): List<Foreldrepenger> =
        sakResponse.map { sak ->
            val sortertePerioder = sak.utbetalinger.sortedByDescending { it.fom }.map { ForeldrepengerPeriode(it.fom, it.tom, it.grad) }
            Foreldrepenger(
                ytelse = sak.ytelse,
                saksnummer = sak.saksnummer,
                perioder = sortertePerioder,
                fom = sortertePerioder.last().fom,
                tom = sortertePerioder.first().tom,
            )
        }

    internal fun hentYtelser(
        fnr: String,
        fraOgMedDato: String?,
        tilOgMedDato: String?,
    ): List<FpSakResponse> {
        val requestBody =
            objectMapper
                .writeValueAsString(
                    YtelserRequest(
                        fnr,
                        fraOgMedDato?.let { LocalDate.parse(it) },
                        tilOgMedDato?.let { LocalDate.parse(it) },
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
        return objectMapper.readValue(
            body,
            objectMapper.typeFactory.constructCollectionType(List::class.java, FpSakResponse::class.java),
        )
    }
}

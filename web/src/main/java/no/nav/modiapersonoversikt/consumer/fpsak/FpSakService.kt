package no.nav.modiapersonoversikt.consumer.fpsak
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

data class ForeldrepengerFpSak(
    val ytelse: YtelseType,
    val saksnummer: String,
    val perioder: List<Utbetaling>,
    val fom: LocalDate,
    val tom: LocalDate,
)

data class YtelserRequest(
    val ident: Ident,
    val fom: LocalDate?,
    val tom: LocalDate?,
)

data class Ident(
    val verdi: String,
)

data class Utbetaling(
    val fom: LocalDate,
    val tom: LocalDate,
    val grad: BigDecimal,
)

enum class YtelseType {
    ENGANGSTØNAD,
    FORELDREPENGER,
    SVANGERSKAPSPENGER,
}

interface FpSakService {
    fun hentYtelserSortertMedPeriode(
        fnr: String,
        fraOgMedDato: String?,
        tilOgMedDato: String?,
    ): List<ForeldrepengerFpSak>
}

open class FpSakServiceImpl(
    private val url: String,
    private val httpClient: OkHttpClient,
) : FpSakService {
    fun hentYtelser(
        fnr: String,
        fraOgMedDato: String?,
        tilOgMedDato: String?,
    ): List<FpSakResponse> {
        val requestBody =
            objectMapper
                .writeValueAsString(
                    YtelserRequest(
                        Ident(verdi = fnr),
                        fraOgMedDato?.let { LocalDate.parse(it) },
                        tilOgMedDato?.let { LocalDate.parse(it) },
                    ),
                ).toRequestBody("application/json".toMediaType())

        val response =
            httpClient
                .newCall(
                    Request
                        .Builder()
                        .url("$url/fpsak/ekstern/api/basis")
                        .post(requestBody)
                        .build(),
                ).execute()

        val body = response.body?.string()
        return objectMapper.readValue(
            body,
            objectMapper.typeFactory.constructCollectionType(List::class.java, ForeldrepengerFpSak::class.java),
        )
    }

    override fun hentYtelserSortertMedPeriode(
        fnr: String,
        fraOgMedDato: String?,
        tilOgMedDato: String?,
    ): List<ForeldrepengerFpSak> {
        val saker = hentYtelser(fnr, fraOgMedDato, tilOgMedDato)
        return saker.map { sak ->
            val sortertePerioder = sak.utbetalinger.sortedByDescending { it.fom }
            ForeldrepengerFpSak(
                ytelse = sak.ytelse,
                saksnummer = sak.saksnummer,
                perioder = sortertePerioder,
                fom = sortertePerioder.last().fom,
                tom = sortertePerioder.first().tom,
            )
        }
    }
}

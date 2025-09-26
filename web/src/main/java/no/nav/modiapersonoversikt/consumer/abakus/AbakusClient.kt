package no.nav.modiapersonoversikt.consumer.abakus

import no.nav.modiapersonoversikt.infrastructure.http.OkHttpUtils.objectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class YtelseV1(
    val version: String,
    val aktor: Aktor,
    val vedtattTidspunkt: LocalDateTime,
    val ytelse: YtelseType,
    val saksnummer: String?,
    val vedtakReferanse: String?,
    val ytelseStatus: Status,
    val kildesystem: Kildesystem?,
    val periode: Periode,
    val tilleggsopplysninger: String?,
    val anvist: List<Anvisning>,
)

data class Aktor(
    val verdi: String,
)

data class Anvisning(
    val periode: Periode,
    val beløp: Desimaltall?,
    val dagsats: Desimaltall?,
    val utbetalingsgrad: Desimaltall?,
)

data class YtelserRequest(
    val ident: Ident,
    val periode: Periode,
    val ytelser: List<YtelseType>,
)

data class Ident(
    val verdi: String,
)

data class Periode(
    val fom: LocalDate,
    val tom: LocalDate,
)

data class Desimaltall(
    val verdi: BigDecimal,
)

enum class Status {
    UNDER_BEHANDLING,
    LØPENDE,
    AVSLUTTET,
    UKJENT,
}

enum class YtelseType {
    /** Folketrygdloven K9 ytelser.  */
    PLEIEPENGER_SYKT_BARN,
    PLEIEPENGER_NÆRSTÅENDE,
    OMSORGSPENGER,
    OPPLÆRINGSPENGER,

    /** Folketrygdloven K14 ytelser.  */
    ENGANGSTØNAD,
    FORELDREPENGER,
    SVANGERSKAPSPENGER,

    /** Midlertidig ytelse for Selvstendig næringsdrivende og Frilansere (Anmodning 10).  */
    FRISINN,
}

enum class Kildesystem {
    FPSAK,
    K9SAK,
}

interface AbakusClient {
    fun hentYtelser(
        fnr: String,
        periode: Periode,
        ytelser: List<YtelseType>,
    ): List<YtelseV1>
}

open class AbakusClientImpl(
    private val url: String,
    private val httpClient: OkHttpClient,
) : AbakusClient {
    override fun hentYtelser(
        fnr: String,
        periode: Periode,
        ytelser: List<YtelseType>,
    ): List<YtelseV1> {
        val requestBody =
            objectMapper
                .writeValueAsString(
                    YtelserRequest(
                        ident = Ident(verdi = fnr),
                        periode = periode,
                        ytelser = ytelser,
                    ),
                ).toRequestBody("application/json".toMediaType())

        val response =
            httpClient
                .newCall(
                    Request
                        .Builder()
                        .url(url)
                        .post(requestBody)
                        .build(),
                ).execute()

        val body = response.body?.string()

        return objectMapper.readValue(body, objectMapper.typeFactory.constructCollectionType(List::class.java, YtelseV1::class.java))
    }
}

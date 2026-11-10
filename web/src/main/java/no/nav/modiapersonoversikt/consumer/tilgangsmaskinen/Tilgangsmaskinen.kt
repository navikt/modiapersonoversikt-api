package no.nav.modiapersonoversikt.consumer.tilgangsmaskinen

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.benmanes.caffeine.cache.Cache
import no.nav.common.types.identer.Fnr
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.generated.infrastructure.*
import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.generated.models.ProblemDetailResponse
import no.nav.modiapersonoversikt.infrastructure.cache.CacheUtils
import no.nav.personoversikt.common.logging.TjenestekallLogger
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

interface Tilgangsmaskinen {
    fun sjekkTilgang(
        veilederIdent: NavIdent,
        fnr: Fnr,
    ): TilgangsMaskinResponse?

    fun sjekkTilganger(
        veilederIdent: NavIdent,
        fnrs: List<String>,
    ): Map<String, TredjepersonTilgangResultat>
}

enum class TredjepersonTilgang {
    GODKJENT,
    AVVIST,
    UAVKLART,
}

data class Tilgangsavslag(
    val title: String?,
    val message: String?,
)

data class TredjepersonTilgangResultat(
    val tilgang: TredjepersonTilgang,
    val avslag: Tilgangsavslag? = null,
)

data class TilgangsMaskinResponse(
    val harTilgang: Boolean,
    val error: ProblemDetailResponse? = null,
)

open class TilgangsmaskinenImpl(
    private val baseUrl: String,
    private val client: OkHttpClient,
    private val tjenestekallLogger: TjenestekallLogger,
    private val objectMapper: ObjectMapper,
    private val cache: Cache<String, TilgangsMaskinResponse?> = CacheUtils.createCache(),
) : Tilgangsmaskinen {
    override fun sjekkTilgang(
        veilederIdent: NavIdent,
        fnr: Fnr,
    ): TilgangsMaskinResponse? =
        cache.get("${veilederIdent.get()}-${fnr.get()}") {
            try {
                val requestBody = fnr.get().toRequestBody("application/json".toMediaTypeOrNull())
                val response =
                    client
                        .newCall(
                            Request
                                .Builder()
                                .url("$baseUrl/api/v1/ccf/komplett/${veilederIdent.get()}")
                                .post(requestBody)
                                .build(),
                        ).execute()

                when {
                    response.isSuccessful -> TilgangsMaskinResponse(harTilgang = true)
                    response.isClientError -> {
                        val errorObject =
                            objectMapper.readValue(response.body?.string(), ProblemDetailResponse::class.java)
                        tjenestekallLogger.error(
                            header = "ClientException ved tilgang sjekking fra tilgangsmaskin",
                            fields =
                                mapOf(
                                    "veilederIdent" to veilederIdent.get(),
                                    "fnr" to fnr.get(),
                                    "message" to response.message,
                                    "error" to errorObject,
                                ),
                        )
                        TilgangsMaskinResponse(harTilgang = false, error = errorObject)
                    }

                    response.isServerError -> {
                        tjenestekallLogger.error(
                            header = "ServerException ved tilgang sjekking fra tilgangsmaskin",
                            fields =
                                mapOf(
                                    "veilederIdent" to veilederIdent.get(),
                                    "fnr" to fnr.get(),
                                    "message" to response.message,
                                ),
                        )
                        TilgangsMaskinResponse(harTilgang = false)
                    }

                    else -> {
                        tjenestekallLogger.error(
                            header = "UnsupportedOperationException ved tilgang sjekking fra tilgangsmaskin",
                            fields =
                                mapOf(
                                    "veilederIdent" to veilederIdent.get(),
                                    "fnr" to fnr.get(),
                                    "message" to response.message,
                                ),
                        )
                        null
                    }
                }
            } catch (e: Exception) {
                tjenestekallLogger.error(
                    "Greide ikke å hente tilgang fra tilgangsmaskinen",
                    throwable = e,
                    fields =
                        mapOf(
                            "veilederIdent" to veilederIdent,
                            "fnr" to fnr,
                        ),
                )
                null
            }
        }

    // TODO før prod: Verifiser at Tilgangsmaskinen har deployet rettingen som hindrer at gyldig enkelttilgang
    //  overstyrer kjerneavslag i bulk-endepunktet. Kjør regresjonstest i dev før merge til master/prod.
    //  Upstream issue-ID er ikke kjent.
    override fun sjekkTilganger(
        veilederIdent: NavIdent,
        fnrs: List<String>,
    ): Map<String, TredjepersonTilgangResultat> {
        val unikeFnrs = fnrs.distinct()
        if (unikeFnrs.isEmpty()) return emptyMap()

        val resultat = LinkedHashMap<String, TredjepersonTilgangResultat>()
        unikeFnrs.chunked(BULK_MAKS_STORRELSE).forEachIndexed { bolkIndeks, bolk ->
            val statuser =
                try {
                    hentBolkStatuser(veilederIdent, bolk, bolkIndeks)
                } catch (e: IOException) {
                    loggBolkFeil(
                        "Teknisk feil ved bulk-tilgangssjekk mot tilgangsmaskinen",
                        bolkIndeks,
                        bolk.size,
                        mapOf("feiltype" to e.javaClass.simpleName),
                    )
                    null
                }
            bolk.forEach { fnr -> resultat[fnr] = statuser?.get(fnr) ?: TredjepersonTilgangResultat(TredjepersonTilgang.UAVKLART) }
        }
        return resultat
    }

    private fun hentBolkStatuser(
        veilederIdent: NavIdent,
        bolk: List<String>,
        bolkIndeks: Int,
    ): Map<String, TredjepersonTilgangResultat>? {
        val url =
            baseUrl
                .toHttpUrl()
                .newBuilder()
                .addPathSegments("api/v1/bulk/ccf")
                .addPathSegment(veilederIdent.get())
                .addPathSegment(KJERNE_REGELTYPE)
                .build()
        val request =
            Request
                .Builder()
                .url(url)
                .post(objectMapper.writeValueAsString(bolk).toRequestBody("application/json".toMediaTypeOrNull()))
                .build()

        return client.newCall(request).execute().use { response ->
            if (response.code != HTTP_MULTI_STATUS) {
                loggBolkFeil(
                    "Uventet HTTP-status ved bulk-tilgangssjekk mot tilgangsmaskinen",
                    bolkIndeks,
                    bolk.size,
                    mapOf("httpStatus" to response.code),
                )
                return@use null
            }
            val json = response.body?.string()?.let { objectMapper.readTree(it) }
            parseBolkRespons(json, veilederIdent, bolk, bolkIndeks)
        }
    }

    private fun parseBolkRespons(
        json: JsonNode?,
        veilederIdent: NavIdent,
        bolk: List<String>,
        bolkIndeks: Int,
    ): Map<String, TredjepersonTilgangResultat>? {
        val ansattId = json?.get("ansattId")
        val resultater = json?.get("resultater")
        if (json == null || !json.isObject || ansattId == null || !ansattId.isTextual || resultater == null || !resultater.isArray) {
            loggBolkFeil("Ugyldig responsformat fra bulk-tilgangssjekk i tilgangsmaskinen", bolkIndeks, bolk.size)
            return null
        }
        if (ansattId.textValue() != veilederIdent.get()) {
            loggBolkFeil("AnsattId i bulk-respons fra tilgangsmaskinen matcher ikke forespørselen", bolkIndeks, bolk.size)
            return null
        }

        val forespurte = bolk.toSet()
        val statuser = mutableMapOf<String, TredjepersonTilgangResultat>()
        val duplikater = mutableSetOf<String>()
        var ukjente = 0
        for (element in resultater) {
            val brukerId = element.get("brukerId")
            val status = element.get("status")
            if (!element.isObject || brukerId == null || !brukerId.isTextual || status == null || !status.isInt) {
                loggBolkFeil("Ugyldig resultatformat fra bulk-tilgangssjekk i tilgangsmaskinen", bolkIndeks, bolk.size)
                return null
            }
            val id = brukerId.textValue()
            if (id !in forespurte) {
                ukjente++
                continue
            }
            val tilgang = tilTilgang(status.intValue())
            val avslag =
                if (tilgang == TredjepersonTilgang.AVVIST) {
                    hentAvslag(element.get("detaljer"), id, veilederIdent, bolkIndeks, bolk.size)
                } else {
                    null
                }
            if (statuser.putIfAbsent(id, TredjepersonTilgangResultat(tilgang, avslag)) != null) {
                duplikater += id
            }
        }
        duplikater.forEach { statuser[it] = TredjepersonTilgangResultat(TredjepersonTilgang.UAVKLART) }

        val mangler = bolk.count { it !in statuser }
        if (ukjente > 0 || duplikater.isNotEmpty() || mangler > 0) {
            tjenestekallLogger.warn(
                header = "Avvik i bulk-respons fra tilgangsmaskinen",
                fields =
                    mapOf(
                        "bolkIndeks" to bolkIndeks,
                        "bolkStorrelse" to bolk.size,
                        "ukjenteIdenter" to ukjente,
                        "duplikateIdenter" to duplikater.size,
                        "manglendeIdenter" to mangler,
                    ),
            )
        }
        return statuser
    }

    private fun hentAvslag(
        detaljer: JsonNode?,
        brukerId: String,
        veilederIdent: NavIdent,
        bolkIndeks: Int,
        bolkStorrelse: Int,
    ): Tilgangsavslag? {
        if (detaljer == null || detaljer.isNull) return null
        if (!detaljer.isObject ||
            listOf("title", "begrunnelse", "brukerIdent", "navIdent").any {
                val felt = detaljer.get(it)
                felt != null && !felt.isNull && !felt.isTextual
            } ||
            detaljer
                .get("brukerIdent")
                ?.takeUnless { it.isNull }
                ?.textValue()
                ?.let { it != brukerId } == true ||
            detaljer
                .get("navIdent")
                ?.takeUnless { it.isNull }
                ?.textValue()
                ?.let { it != veilederIdent.get() } == true
        ) {
            loggBolkFeil("Ugyldige avslagsdetaljer fra bulk-tilgangssjekk i tilgangsmaskinen", bolkIndeks, bolkStorrelse)
            return null
        }
        val title = detaljer.get("title")?.textValue()?.takeIf { it.isNotBlank() }
        val message = detaljer.get("begrunnelse")?.textValue()?.takeIf { it.isNotBlank() }
        return if (title == null && message == null) null else Tilgangsavslag(title, message)
    }

    private fun tilTilgang(status: Int): TredjepersonTilgang =
        when (status) {
            204 -> TredjepersonTilgang.GODKJENT
            403 -> TredjepersonTilgang.AVVIST
            else -> TredjepersonTilgang.UAVKLART
        }

    private fun loggBolkFeil(
        header: String,
        bolkIndeks: Int,
        bolkStorrelse: Int,
        ekstraFelter: Map<String, Any?> = emptyMap(),
    ) {
        tjenestekallLogger.error(
            header = header,
            fields = mapOf("bolkIndeks" to bolkIndeks, "bolkStorrelse" to bolkStorrelse) + ekstraFelter,
        )
    }

    companion object {
        const val BULK_MAKS_STORRELSE = 1000
        private const val KJERNE_REGELTYPE = "KJERNE_REGELTYPE"
        private const val HTTP_MULTI_STATUS = 207
    }
}

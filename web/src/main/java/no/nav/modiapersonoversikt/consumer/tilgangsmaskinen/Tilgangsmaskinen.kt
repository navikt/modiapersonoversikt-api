package no.nav.modiapersonoversikt.consumer.tilgangsmaskinen

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.benmanes.caffeine.cache.Cache
import no.nav.common.types.identer.Fnr
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.generated.infrastructure.*
import no.nav.modiapersonoversikt.consumer.tilgangsmaskinen.generated.models.ProblemDetailResponse
import no.nav.modiapersonoversikt.infrastructure.cache.CacheUtils
import no.nav.personoversikt.common.logging.TjenestekallLogger
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

interface Tilgangsmaskinen {
    fun sjekkTilgang(
        veilederIdent: NavIdent,
        fnr: Fnr,
    ): TilgangsMaskinResponse?
}

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
                val requestBody =
                    fnr
                        .get()
                        .toRequestBody("application/json".toMediaTypeOrNull())

                val response =
                    client
                        .newCall(
                            Request
                                .Builder()
                                .url("$baseUrl/api/v1/ccf/komplett/$veilederIdent")
                                .post(requestBody)
                                .build(),
                        ).execute()

                if (response.isSuccessful) {
                    TilgangsMaskinResponse(harTilgang = true)
                } else if (response.isClientError) {
                    val errorObject =
                        objectMapper.readValue(response.body as String, ProblemDetailResponse::class.java)
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
                } else if (response.isServerError) {
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
                } else {
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
            } catch (e: Exception) {
                tjenestekallLogger.error(
                    "Greide ikke å hente tilgang fra tilgangsmaskinen",
                    throwable = e,
                    fields = mapOf("veilederIdent" to veilederIdent.get(), "fnr" to fnr.get()),
                )
                null
            }
        }
}

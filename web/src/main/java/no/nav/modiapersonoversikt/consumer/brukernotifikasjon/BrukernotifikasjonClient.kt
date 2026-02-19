package no.nav.modiapersonoversikt.consumer.brukernotifikasjon

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.infrastructure.http.OkHttpUtils.objectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class BrukernotifikasjonClient(
    val baseUrl: String,
    val httpClient: OkHttpClient,
) : Brukernotifikasjon.Client {
    override fun hentAlleBrukernotifikasjoner(fnr: Fnr): List<Brukernotifikasjon.Event> {
        val requestBody =
            objectMapper
                .writeValueAsString(mapOf("ident" to fnr.get()))
                .toRequestBody("application/json".toMediaType())

        val response =
            httpClient
                .newCall(
                    Request
                        .Builder()
                        .post(requestBody)
                        .url("$baseUrl/varsel/alle")
                        .build(),
                ).execute()

        val bodyContent = checkNotNull(response.body?.string()) { "No Content" }

        return objectMapper.readValue(bodyContent)
    }
}

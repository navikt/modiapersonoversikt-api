package no.nav.modiapersonoversikt.consumer.brukernotifikasjon

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.rest.client.RestClient
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.infrastructure.http.*
import okhttp3.OkHttpClient
import okhttp3.Request

class BrukernotifikasjonClient(val baseUrl: String, authInterceptor: HeadersInterceptor) : Brukernotifikasjon.Client {
    private val httpClient: OkHttpClient = RestClient.baseClient().newBuilder()
        .addInterceptor(XCorrelationIdInterceptor())
        .addInterceptor(
            LoggingInterceptor("Brukernotifikasjon") { request ->
                requireNotNull(request.header("X-Correlation-ID")) {
                    "Kall uten \"X-Correlation-ID\" er ikke lov"
                }
            }
        )
        .addInterceptor(authInterceptor)
        .build()

    override fun hentBrukernotifikasjoner(type: Brukernotifikasjon.Type, fnr: Fnr): List<Brukernotifikasjon.Event> {
        val response = httpClient
            .newCall(
                Request
                    .Builder()
                    .get()
                    .url("$baseUrl/${type.name.lowercase()}/all")
                    .header("fodselsnummer", fnr.get())
                    .build()
            )
            .execute()

        val bodyContent = checkNotNull(response.body()?.string()) { "No Content" }

        return OkHttpUtils.objectMapper.readValue(bodyContent)
    }
}

package no.nav.modiapersonoversikt.consumer.sak

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.rest.client.RestClient
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.OkHttpUtils
import no.nav.modiapersonoversikt.infrastructure.http.OkHttpUtils.MediaTypes
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.time.OffsetDateTime

interface SakApi {
    fun hentSaker(aktorId: String): List<SakDto>
    fun opprettSak(sak: OpprettSakDto): SakDto
}

class SakApiImpl(
    private val baseUrl: String = EnvironmentUtils.getRequiredProperty("SAK_ENDPOINTURL"),
    private val stsService: SystemUserTokenProvider
) : SakApi {
    private val objectMapper = OkHttpUtils.objectMapper
    private val client = RestClient.baseClient().newBuilder()
        .addInterceptor(XCorrelationIdInterceptor())
        .addInterceptor(
            AuthorizationInterceptor {
                stsService.systemUserToken // TODO må undersøke om vi kan bruke veileders token istedet
            }
        )
        .addInterceptor(
            LoggingInterceptor("Sak") { request ->
                requireNotNull(request.header("X-Correlation-ID")) {
                    "Kall uten \"X-Correlation-ID\" er ikke lov"
                }
            }
        )
        .build()

    override fun hentSaker(aktorId: String): List<SakDto> {
        val request = Request
            .Builder()
            .url("$baseUrl/api/v1/saker?aktoerId=$aktorId")
            .header("accept", "application/json")
            .build()

        return fetch(request)
    }

    override fun opprettSak(sak: OpprettSakDto): SakDto {
        val requestBody = RequestBody.create(
            MediaTypes.JSON,
            objectMapper.writeValueAsString(sak)
        )
        val request = Request
            .Builder()
            .url("$baseUrl/api/v1/saker")
            .header("accept", "application/json")
            .post(requestBody)
            .build()

        return fetch(request)
    }

    private inline fun <reified RESPONSE> fetch(request: Request): RESPONSE {
        val response: Response = client
            .newCall(request)
            .execute()

        val body = response.body()?.string()

        return if (response.code() in 200..299 && body != null) {
            objectMapper.readValue(body)
        } else {
            throw IllegalStateException("Forventet 200-range svar og body fra sak-api, men fikk: ${response.code()} $body")
        }
    }
}

data class OpprettSakDto(
    val aktoerId: String,
    val tema: String,
    val fagsakNr: String? = null,
    val applikasjon: String,
    val opprettetAv: String
    // val orgnr: String, // Vi oppretter ikke saker tilknyttet foretak, så denne skal aldri være satt i modia
)

data class SakDto(
    val id: String? = null,
    val tema: String? = null, // example: AAP
    val applikasjon: String? = null, // example: IT01 Kode for applikasjon iht. felles kodeverk
    val aktoerId: String? = null, // example: 10038999999 Id til aktøren saken gjelder
    val orgnr: String? = null, // Orgnr til foretaket saken gjelder
    val fagsakNr: String? = null, // Fagsaknr for den aktuelle saken - hvis aktuelt
    val opprettetAv: String? = null, // Brukerident til den som opprettet saken
    val opprettetTidspunkt: OffsetDateTime? = null
)

package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.rest.client.RestClient
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.EnvironmentUtils
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.http.AuthorizationInterceptor
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.http.LoggingInterceptor
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.http.OkHttpUtils
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.http.OkHttpUtils.MediaTypes
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.http.XCorrelationIdInterceptor
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.time.ZonedDateTime

interface SakApiGateway {
    fun hentSaker(aktorId: String): List<SakDto>
    fun opprettSak(sak: SakDto): SakDto
}

class SakApiGatewayImpl(
    private val baseUrl: String = EnvironmentUtils.getRequiredProperty("SAK_ENDPOINTURL"),
    private val stsService: SystemUserTokenProvider
) : SakApiGateway {
    private val objectMapper = OkHttpUtils.objectMapper
    private val client = RestClient.baseClient().newBuilder()
        .addInterceptor(XCorrelationIdInterceptor())
        .addInterceptor(AuthorizationInterceptor {
            stsService.systemUserToken
        })
        .addInterceptor(LoggingInterceptor("Sak") { request ->
            requireNotNull(request.header("X-Correlation-ID")) {
                "Kall uten \"X-Correlation-ID\" er ikke lov"
            }
        })
        .build()

    override fun hentSaker(aktorId: String): List<SakDto> {
        val request = Request
            .Builder()
            .url("$baseUrl/api/v1/saker?aktoerId=$aktorId")
            .header("accept", "application/json")
            .build()

        return fetch(request)
    }

    override fun opprettSak(sak: SakDto): SakDto {
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

data class SakDto(
    val id: String? = null,
    val tema: String? = null, //example: AAP
    val applikasjon: String? = null, //example: IT01 Kode for applikasjon iht. felles kodeverk
    val aktoerId: String? = null, //example: 10038999999 Id til aktøren saken gjelder
    val orgnr: String? = null, //Orgnr til foretaket saken gjelder
    val fagsakNr: String? = null, //Fagsaknr for den aktuelle saken - hvis aktuelt
    val opprettetAv: String? = null, //Brukerident til den som opprettet saken
    val opprettetTidspunkt: ZonedDateTime? = null
)


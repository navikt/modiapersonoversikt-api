package no.nav.modiapersonoversiktproxy.consumer.ereg

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.rest.client.RestClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversiktproxy.infrastructure.LoggingInterceptor
import no.nav.modiapersonoversiktproxy.infrastructure.RestConstants
import no.nav.modiapersonoversiktproxy.infrastructure.getCallId
import no.nav.modiapersonoversiktproxy.utils.AppConstants
import no.nav.personoversikt.common.logging.TjenestekallLogg
import okhttp3.Request
import okhttp3.Response
import org.slf4j.LoggerFactory
import java.util.*

interface OrganisasjonV1Client {
    fun hentNokkelInfo(orgnummer: String): OrganisasjonResponse?
}

class OrganisasjonV1ClientImpl(baseUrl: String = EnvironmentUtils.getRequiredProperty("EREG_ENDPOINTURL")) :
    OrganisasjonV1Client {
    private val url = baseUrl + "api/v1/organisasjon/"
    private val log = LoggerFactory.getLogger(OrganisasjonV1ClientImpl::class.java)
    private val client =
        RestClient.baseClient()
            .newBuilder()
            .addInterceptor(
                LoggingInterceptor("Ereg-services") { request ->
                    requireNotNull(request.header(RestConstants.NAV_CALL_ID_HEADER)) {
                        "Kall uten \"${RestConstants.NAV_CALL_ID_HEADER}\" er ikke lov"
                    }
                },
            )
            .build()
    private val objectMapper =
        jacksonObjectMapper()
            .apply { configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) }

    override fun hentNokkelInfo(orgnummer: String): OrganisasjonResponse? {
        val uuid = UUID.randomUUID()
        try {
            TjenestekallLogg.info(
                "Organisasjon-request: $uuid",
                mapOf(
                    "orgnummer" to orgnummer,
                    "callId" to getCallId(),
                ),
            )
            val response: Response =
                client
                    .newCall(
                        Request.Builder()
                            .url("$url$orgnummer/noekkelinfo")
                            .header(RestConstants.NAV_CALL_ID_HEADER, getCallId())
                            .header(RestConstants.NAV_CONSUMER_ID_HEADER, AppConstants.SYSTEMUSER_USERNAME)
                            .header("accept", "application/json")
                            .build(),
                    )
                    .execute()
            val body = response.body?.string()
            val tjenestekallInfo =
                mapOf(
                    "status" to "${response.code} ${response.message}",
                    "orgnummer" to orgnummer,
                    "body" to body,
                )

            return if (response.code in 200..299 && body != null) {
                TjenestekallLogg.info("Ereg hent orgnavn-response: $uuid", tjenestekallInfo)
                objectMapper.readValue(body)
            } else {
                TjenestekallLogg.error("Ereg hent orgnavn-response-error: $uuid", tjenestekallInfo)
                null
            }
        } catch (exception: Exception) {
            log.error("Feilet ved GET kall mot ereg  (ID: $uuid)", exception)
            TjenestekallLogg.error(
                "ereg orgnavn-error: $uuid",
                mapOf(
                    "exception" to exception,
                    "orgnummer" to orgnummer,
                ),
            )
            return null
        }
    }
}

data class OrgNavn(
    val redigertnavn: String? = null,
    val navnelinje1: String? = null,
    val navnelinje2: String? = null,
    val navnelinje3: String? = null,
    val navnelinje4: String? = null,
    val navnelinje5: String? = null,
)

data class OrganisasjonResponse(
    val organisasjonsnummer: String,
    val navn: OrgNavn,
)

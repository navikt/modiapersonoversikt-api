package no.nav.kjerneinfo.consumer.organisasjon

import com.google.gson.GsonBuilder
import no.nav.common.log.MDCConstants
import no.nav.common.rest.client.RestClient
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.EnvironmentUtils
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestConstants
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.TjenestekallLogger
import okhttp3.Request
import okhttp3.Response
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

interface OrganisasjonV1RestClient {
    fun hentKjernInfoFraRestClient(orgnummer: String): OrganisasjonResponse
}

open class OrganisasjonRestClientImpl @Autowired constructor(
        var stsService: SystemUserTokenProvider
) : OrganisasjonV1RestClient {
    val ORG_BASEURL = EnvironmentUtils.getRequiredProperty("EREG_ENDPOINTURL")
    val url = ORG_BASEURL + "api/v1/organisasjon/"
    private val log = LoggerFactory.getLogger(OrganisasjonRestClientImpl::class.java)
    private val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()


    override fun hentKjernInfoFraRestClient(orgnummer: String): OrganisasjonResponse {
        val uuid = UUID.randomUUID()
        try {

            val consumerOidcToken: String = stsService.systemUserToken
            TjenestekallLogger.info("Oppgaver-request: $uuid", mapOf(
                    "orgnummer" to orgnummer,
                    "callId" to MDC.get(MDCConstants.MDC_CALL_ID)
            ))
            val response: Response = RestClient.baseClient()
                    .newCall(
                            Request.Builder()
                                    .url("$url$orgnummer/noekkelinfo")
                                    .header(RestConstants.NAV_CALL_ID_HEADER, MDC.get(MDCConstants.MDC_CALL_ID))
                                    .header("X-Correlation-ID", MDC.get(MDCConstants.MDC_CALL_ID))
                                    .header(RestConstants.AUTHORIZATION, RestConstants.AUTH_METHOD_BEARER + RestConstants.AUTH_SEPERATOR + consumerOidcToken)
                                    .header(RestConstants.NAV_CONSUMER_TOKEN_HEADER, RestConstants.AUTH_METHOD_BEARER + RestConstants.AUTH_SEPERATOR + consumerOidcToken)
                                    .header("accept", "application/json")
                                    .build()
                    )
                    .execute()
            val body = response.body()?.string()
            validateResponse(response, uuid, body, orgnummer)
            return gson.fromJson(body, OrganisasjonResponse::class.java)
        } catch (exception: Exception) {
            log.error("Feilet ved GET kall mot ereg  (ID: $uuid)", exception)
            TjenestekallLogger.error("ereg orgnavn-error: $uuid", mapOf(
                    "exception" to exception,
                    "orgnummer" to orgnummer
            ))
            throw exception
        }
    }

    private fun validateResponse(response: Response, uuid: UUID?, body: String?, orgnummer: String) {
        if (response.code() in 200..299) {
            TjenestekallLogger.info("Ereg hent orgnavn-response: $uuid", mapOf(
                    "status" to "${response.code()} ${response.message()}",
                    "body" to body
            ))
        } else {
            TjenestekallLogger.error("Ereg hent orgnavn-response-error: $uuid", mapOf(
                    "status" to "${response.code()} ${response.message()}",
                    "orgnummer" to orgnummer,
                    "body" to body
            ))
        }
    }
}

data class OrgNavn(
        val redigertnavn: String,
        val navnelinje1: String
)

data class OrganisasjonResponse(
        val organisasjonsnummer: String,
        val navn: OrgNavn
)

package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation

import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.log.MDCConstants
import no.nav.common.rest.client.RestClient
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.EnvironmentUtils
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestConstants
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestConstants.MODIABRUKERDIALOG_SYSTEM_USER
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.TjenestekallLogger
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.FodselnummerAktorService
import okhttp3.Request
import okhttp3.Response
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import java.util.*


internal interface SakApiGateway {
    fun hentSaker(fnr: String): List<SakDto>
}

internal class SakApiGatewayImpl(val fodselnummerAktorService: FodselnummerAktorService,
                                 val baseUrl: String = EnvironmentUtils.getRequiredProperty("SAK_ENDPOINTURL"),
                                 val stsService: SystemUserTokenProvider
                                ) : SakApiGateway {


    val url = baseUrl + "/api/v1/saker"
    private val log = LoggerFactory.getLogger(SakApiGatewayImpl::class.java)
    private val client = RestClient.baseClient()
    private val objectMapper = jacksonObjectMapper()

    override fun hentSaker(fnr: String): List<SakDto> {
        objectMapper.registerModule(JodaModule())
        val uuid = UUID.randomUUID()
        try {
            val consumerOidcToken: String = stsService.systemUserToken
            val aktoerId = fodselnummerAktorService.hentAktorIdForFnr(fnr)
            TjenestekallLogger.info("Oppgaver-request: $uuid", mapOf(
                    "orgnummer" to fnr,
                    "callId" to MDC.get(MDCConstants.MDC_CALL_ID)
            ))
            val response: Response = client
                    .newCall(
                            Request.Builder()
                                    .url("$url?aktoerId=$aktoerId")
                                    .header(RestConstants.NAV_CALL_ID_HEADER, MDC.get(MDCConstants.MDC_CALL_ID)
                                            ?: UUID.randomUUID().toString())
                                    .header("X-Correlation-ID", MDC.get(MDCConstants.MDC_CALL_ID))
                                    .header(RestConstants.AUTHORIZATION, RestConstants.AUTH_METHOD_BEARER + RestConstants.AUTH_SEPERATOR + consumerOidcToken)
                                    .header(RestConstants.NAV_CONSUMER_TOKEN_HEADER, RestConstants.AUTH_METHOD_BEARER + RestConstants.AUTH_SEPERATOR + consumerOidcToken)
                                    .header(RestConstants.NAV_CONSUMER_ID_HEADER, MODIABRUKERDIALOG_SYSTEM_USER)
                                    .header("accept", "application/json")
                                    .build()
                    )
                    .execute()
            val body = response.body()?.string()
            val tjenestekallInfo = mapOf(
                    "status" to "${response.code()} ${response.message()}",
                    "fnr" to fnr,
                    "body" to body
            )

            if (response.code() in 200..299 && body != null) {
                TjenestekallLogger.info("hent saker-response: $uuid", tjenestekallInfo)
                return objectMapper.readValue(body)
            } else {
                TjenestekallLogger.error("hent sak-response-error: $uuid", tjenestekallInfo)

            }
        } catch (exception: Exception) {
            log.error("Feilet ved GET kall mot Sak  (ID: $uuid)", exception)
            TjenestekallLogger.error("Sak-error: $uuid", mapOf(
                    "exception" to exception,
                    "fnr" to fnr
            ))

        }
        return emptyList()
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
        val opprettetTidspunkt: org.joda.time.DateTime? = null)


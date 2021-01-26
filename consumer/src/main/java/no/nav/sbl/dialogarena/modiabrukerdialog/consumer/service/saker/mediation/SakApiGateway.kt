package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.log.MDCConstants
import no.nav.common.rest.client.RestClient
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.EnvironmentUtils
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentIdent
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestConstants
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.TjenestekallLogger
import okhttp3.Request
import okhttp3.Response
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_DATE_TIME
import java.util.*


internal interface SakApiGateway {
    fun hentSaker(fnr: String): List<SakDto>
}

internal class SakApiGatewayImpl(val pdlOppslagService: PdlOppslagService,
                                 val baseUrl: String = EnvironmentUtils.getRequiredProperty("SAK_ENDPOINTURL"),
                                 val stsService: SystemUserTokenProvider
) : SakApiGateway {


    private val log = LoggerFactory.getLogger(SakApiGatewayImpl::class.java)
    private val client = RestClient.baseClient()
    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule().addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer(ISO_DATE_TIME)))
    }

    override fun hentSaker(fnr: String): List<SakDto> {
        val callId = MDC.get(MDCConstants.MDC_CALL_ID) ?: UUID.randomUUID().toString()
        try {
            val consumerOidcToken: String = stsService.systemUserToken
            val identliste = pdlOppslagService.hentIdent(fnr)

            TjenestekallLogger.info("sakapi-request: $callId", mapOf(
                    "fnr" to fnr,
                    "callId" to callId)
            )
            val aktoerId = identliste
                    ?.identer
                    ?.find { it -> it.gruppe == HentIdent.IdentGruppe.AKTORID }
                    ?.ident ?: throw Exception("PDL Oppslag feilet for å hente aktørID")

            val response: Response = client
                    .newCall(
                            Request.Builder()
                                    .url("$baseUrl/api/v1/saker?aktoerId=$aktoerId")
                                    .header("X-Correlation-ID", callId)
                                    .header(RestConstants.AUTHORIZATION, RestConstants.AUTH_METHOD_BEARER + RestConstants.AUTH_SEPERATOR + consumerOidcToken)
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
                TjenestekallLogger.info("hent saker-api-response: $callId", tjenestekallInfo)
                return objectMapper.readValue(body)
            } else {
                TjenestekallLogger.error("hent saker-api-response-error: $callId", tjenestekallInfo)

            }
        } catch (exception: Exception) {
            log.error("Feilet ved GET kall mot Sak API  (ID: $callId)", exception)
            TjenestekallLogger.error("Sak-error: $callId", mapOf(
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
        val opprettetTidspunkt: java.time.LocalDateTime? = null)


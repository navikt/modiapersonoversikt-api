package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgave

import com.google.gson.GsonBuilder
import no.nav.common.log.MDCConstants
import no.nav.common.rest.client.RestClient
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.EnvironmentUtils
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.apis.OppgaveApi
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.models.PostOppgaveRequestJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OpprettOppgaveRequest
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveRestClient
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentIdent.IdentGruppe
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveResponse
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.KodeverksmapperService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.domain.Behandling
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.pdl.PdlSyntetiskMapper
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.RestConstants
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.RestConstants.AUTH_METHOD_BEARER
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.RestConstants.AUTH_SEPERATOR
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.TjenestekallLogger
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.slf4j.MDC
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.*
import org.springframework.beans.factory.annotation.Autowired

open class OppgaveOpprettelseClient @Autowired constructor(
        val kodeverksmapperService: KodeverksmapperService,
        val pdlOppslagService: PdlOppslagService
) : OppgaveRestClient {
    val OPPGAVE_BASEURL = EnvironmentUtils.getRequiredProperty("OPPGAVE_BASEURL")
    val client = OppgaveApi(OPPGAVE_BASEURL)
    val url = OPPGAVE_BASEURL + "api/v1/oppgaver"
    private val log = LoggerFactory.getLogger(OppgaveOpprettelseClient::class.java)
    private val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()

    @Autowired
    private lateinit var stsService: SystemUserTokenProvider



    private fun opprettOppgave(request: PostOppgaveRequestJsonDTO) : OppgaveResponse{
        val consumerOidcToken: String = stsService.systemUserToken
        val response = client.opprettOppgave(
                authorization = consumerOidcToken,
                xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                postOppgaveRequestJsonDTO = request
        )
        return OppgaveResponse(response.id?.toString() ?: throw RuntimeException("No oppgaveId found"))
    }
    override fun opprettSkjermetOppgave(opprettOppgave: OpprettOppgaveRequest) : OppgaveResponse {
        val behandling: Optional<Behandling> = kodeverksmapperService.mapUnderkategori(opprettOppgave.underkategoriKode)
        val aktorId = getAktorId(opprettOppgave.fnr)
        if (aktorId == null || aktorId.isEmpty()) {
            throw Exception("AktørId-mangler på person")
        }

        val request = PostOppgaveRequestJsonDTO(
                opprettetAvEnhetsnr = opprettOppgave.opprettetavenhetsnummer,
                aktoerId = aktorId,
                behandlesAvApplikasjon = "FS22",
                beskrivelse = opprettOppgave.beskrivelse,
                temagruppe = "",
                tema = opprettOppgave.tema,
                behandlingstema = behandling.map(Behandling::getBehandlingstema).orElse(null),
                oppgavetype = kodeverksmapperService.mapOppgavetype(opprettOppgave.oppgavetype),
                behandlingstype = behandling.map(Behandling::getBehandlingstype).orElse(null),
                aktivDato = LocalDate.now(),
                fristFerdigstillelse = opprettOppgave.oppgaveFrist,
                prioritet = PostOppgaveRequestJsonDTO.Prioritet.valueOf(stripTemakode(opprettOppgave.prioritet))
        )

        return opprettOppgave(request)
    }


    override fun opprettOppgave(opprettOppgave: OpprettOppgaveRequest): OppgaveResponse {
        //Mapping fra gammel kodeverk som frontend bruker til nytt kodeverk som Oppgave bruker
        val behandling: Optional<Behandling> = kodeverksmapperService.mapUnderkategori(opprettOppgave.underkategoriKode)
        val oppgaveTypeMapped: String = kodeverksmapperService.mapOppgavetype(opprettOppgave.oppgavetype)
        val aktorId = getAktorId(opprettOppgave.fnr)
        if (aktorId == null || aktorId.isEmpty()) {
            throw Exception("AktørId-mangler på person")
        }

        val request = PostOppgaveRequestJsonDTO(
                opprettetAvEnhetsnr = opprettOppgave.opprettetavenhetsnummer,
                aktoerId = aktorId,
                behandlesAvApplikasjon = "FS22",
                beskrivelse = opprettOppgave.beskrivelse,
                temagruppe = opprettOppgave.temagruppe,
                tema = opprettOppgave.tema,
                behandlingstema = behandling.map(Behandling::getBehandlingstema).orElse(null),
                oppgavetype = oppgaveTypeMapped,
                behandlingstype = behandling.map(Behandling::getBehandlingstype).orElse(null),
                aktivDato = LocalDate.now(),
                fristFerdigstillelse = opprettOppgave.oppgaveFrist,
                prioritet = PostOppgaveRequestJsonDTO.Prioritet.valueOf(stripTemakode(opprettOppgave.prioritet))
        )

        return opprettOppgave(request)
    }


    private fun gjorSporring(url: String, request: OppgaveSkjermetRequestDTO): OppgaveResponse {
        val uuid = UUID.randomUUID()
        try {

            val consumerOidcToken: String = stsService.systemUserToken
            TjenestekallLogger.info("Oppgaver-request: $uuid", mapOf(
                    "ident" to request.aktoerId,
                    "callId" to MDC.get(MDCConstants.MDC_CALL_ID)
            ))
            val response: Response = RestClient.baseClient()
                    .newCall(
                            Request.Builder()
                                    .url(url)
                                    .header(RestConstants.NAV_CALL_ID_HEADER, MDC.get(MDCConstants.MDC_CALL_ID))
                                    .header("X-Correlation-ID", MDC.get(MDCConstants.MDC_CALL_ID))
                                    .header(RestConstants.AUTHORIZATION, AUTH_METHOD_BEARER + AUTH_SEPERATOR + consumerOidcToken)
                                    .header(RestConstants.NAV_CONSUMER_TOKEN_HEADER, RestConstants.AUTH_METHOD_BEARER + RestConstants.AUTH_SEPERATOR + consumerOidcToken)
                                    .post(RequestBody.create(
                                            MediaType.parse("application/json"),
                                            gson.toJson(request)
                                    ))
                                    .build()
                    )
                    .execute()
            val body = response.body()?.string()
            if (response.code() in 200..299) {
                TjenestekallLogger.info("Oppgave-response: $uuid", mapOf(
                        "status" to "${response.code()} ${response.message()}",
                        "body" to body
                ))
            } else {
                TjenestekallLogger.error("Oppgave-response-error: $uuid", mapOf(
                        "status" to "${response.code()} ${response.message()}",
                        "request" to request,
                        "body" to body
                ))
            }
            return gson.fromJson(body, OppgaveResponse::class.java)
        } catch (exception: Exception) {
            log.error("Feilet ved post mot Oppgave (ID: $uuid)", exception)
            TjenestekallLogger.error("Oppgave-error: $uuid", mapOf(
                    "exception" to exception,
                    "request" to request
            ))
            throw exception
        }
    }


    private fun getAktorId(fnr: String): String? {
        return try {
            pdlOppslagService
                    .hentIdent(fnr)
                    ?.identer
                    ?.find { ident -> ident.gruppe == IdentGruppe.AKTORID }
                    ?.ident
                    // syntmapping for Q2 --> Q1
                    ?.let(PdlSyntetiskMapper::mapAktorIdFraPdl)
        } catch (exception: Exception) {
            null
        }
    }

    private fun stripTemakode(prioritet: String): String {
        return prioritet.substringBefore("_")
    }
}

data class OppgaveSkjermetRequestDTO(
        val opprettetAvEnhetsnr: String?,
        val aktoerId: String?,
        val behandlesAvApplikasjon: String?,
        val beskrivelse: String?,
        val temagruppe: String?,
        val tema: String?,
        val behandlingstema: String?,
        val oppgavetype: String,
        val behandlingstype: String?,
        val aktivDato: String,
        val fristFerdigstillelse: String,
        val prioritet: String

)

data class OppaveSkjermetResponsDTO(
        val aktoerId: String?,
        val beskrivelse: String?,
        val temagruppe: String?,
        val tema: String?,
        val behandlingstema: String?,
        val oppgavetype: String,
        val behandlingstype: String?,

        val fristFerdigstillelse: LocalDate?,
        val aktivDato: LocalDate,
        val opprettetTidspunkt: LocalDate,
        val prioritet: String
)

enum class Prioritet {
    NORM, LAV, HOY
}


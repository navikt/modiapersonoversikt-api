package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgave

import com.google.gson.GsonBuilder
import no.nav.common.log.MDCConstants
import no.nav.common.rest.client.RestClient
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.EnvironmentUtils
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.apis.OppgaveApi
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentIdent.IdentGruppe
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.*
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
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate
import java.util.*

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


    override fun opprettSkjermetOppgave(oppgave: OpprettSkjermetOppgaveRequest): OpprettOppgaveResponse {
        //Mapping fra gammel kodeverk som frontend bruker til nytt kodeverk som Oppgave bruker
        val behandling: Optional<Behandling> = kodeverksmapperService.mapUnderkategori(oppgave.underkategoriKode)
        val oppgaveTypeMapped: String = kodeverksmapperService.mapOppgavetype(oppgave.oppgavetype)
        val aktorId = getAktorId(oppgave.fnr)
        if (aktorId == null || aktorId.isEmpty()) {
            throw Exception("AktørId-mangler på person")
        }

        val oppgaveskjermetObject = OppgaveSkjermetRequestDTO(
                opprettetAvEnhetsnr = oppgave.opprettetavenhetsnummer,
                aktoerId = aktorId,
                behandlesAvApplikasjon = "FS22",
                beskrivelse = oppgave.beskrivelse,
                temagruppe = "",
                tema = oppgave.tema,
                behandlingstema = behandling.map(Behandling::getBehandlingstema).orElse(null),
                oppgavetype = oppgaveTypeMapped,
                behandlingstype = behandling.map(Behandling::getBehandlingstype).orElse(null),
                aktivDato = LocalDate.now().toString(),
                fristFerdigstillelse = oppgave.oppgaveFrist.toString(),
                prioritet = stripTemakode(oppgave.prioritet)
        )
        return gjorSporring(url, oppgaveskjermetObject)

    }


    private fun gjorSporring(url: String, request: OppgaveSkjermetRequestDTO): OpprettOppgaveResponse {
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
            return gson.fromJson(body, OpprettOppgaveResponse::class.java)
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

enum class Prioritet {
    NORM, LAV, HOY
}


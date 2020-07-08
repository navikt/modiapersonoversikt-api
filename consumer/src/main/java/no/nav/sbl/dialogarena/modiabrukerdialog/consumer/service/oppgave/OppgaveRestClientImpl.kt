package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgave

import com.google.gson.GsonBuilder
import no.nav.common.oidc.SystemUserTokenProvider
import no.nav.log.MDCConstants
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveRequest
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveRestClient
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.oppgave.OppgaveResponse
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.generated.HentIdent.IdentGruppe
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.KodeverksmapperService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.domain.Behandling
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.pdl.PdlSyntetiskMapper
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.RestConstants
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.RestConstants.AUTH_METHOD_BEARER
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.RestConstants.AUTH_SEPERATOR
import no.nav.sbl.rest.RestUtils
import no.nav.sbl.util.EnvironmentUtils
import org.slf4j.MDC
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.*
import javax.inject.Inject
import javax.ws.rs.client.Entity.json
import javax.ws.rs.core.HttpHeaders.AUTHORIZATION


open class OppgaveOpprettelseClient @Inject constructor(
        val kodeverksmapperService: KodeverksmapperService,
        val pdlOppslagService: PdlOppslagService


) : OppgaveRestClient {
    val OPPGAVE_BASEURL = EnvironmentUtils.getRequiredProperty("OPPGAVE_BASEURL")
    val url = OPPGAVE_BASEURL + "api/v1/oppgaver"
    private val log = LoggerFactory.getLogger(OppgaveOpprettelseClient::class.java)
    private val tjenestekallLogg = LoggerFactory.getLogger("SecureLog")
    private val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()

    @Inject
    private lateinit var stsService: SystemUserTokenProvider


    override fun opprettOppgave(oppgave: OppgaveRequest): OppgaveResponse {
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
                behandlingstema = behandling?.map(Behandling::getBehandlingstema).orElse(null),
                oppgavetype = oppgaveTypeMapped,
                behandlingstype = behandling?.map(Behandling::getBehandlingstype).orElse(null),
                aktivDato = LocalDate.now().toString(),
                fristFerdigstillelse = oppgave.oppgaveFrist.toString(),
                prioritet = stripTemakode(oppgave.prioritet)
        )
        return gjorSporring(url, oppgaveskjermetObject)

    }


    private fun gjorSporring(url: String, request: OppgaveSkjermetRequestDTO): OppgaveResponse {
        val uuid = UUID.randomUUID()
        try {

            val consumerOidcToken: String = stsService.systemUserAccessToken
            tjenestekallLogg.info("""
            Oppgaver-request: $uuid
            ------------------------------------------------------------------------------------
                ident: ${request.aktoerId}
                callId: ${MDC.get(MDCConstants.MDC_CALL_ID)}
            ------------------------------------------------------------------------------------
        """.trimIndent())

            val content: String = RestUtils.withClient { client ->
                val response = client.target(url)
                        .request()
                        .header(RestConstants.NAV_CALL_ID_HEADER, MDC.get(MDCConstants.MDC_CALL_ID))
                        .header("X-Correlation-ID ", MDC.get(MDCConstants.MDC_CALL_ID))
                        .header(AUTHORIZATION, AUTH_METHOD_BEARER + AUTH_SEPERATOR + consumerOidcToken)
                        .header(RestConstants.NAV_CONSUMER_TOKEN_HEADER, RestConstants.AUTH_METHOD_BEARER + RestConstants.AUTH_SEPERATOR + consumerOidcToken)
                        .post(json(request))

                val body = response.readEntity(String::class.java)
                tjenestekallLogg.info("""
                Oppgave-response: $uuid
                ------------------------------------------------------------------------------------
                    status: ${response.status} ${response.statusInfo}
                    body: $body
                ------------------------------------------------------------------------------------
            """.trimIndent())

                body
            }
            return gson.fromJson(content, OppgaveResponse::class.java)
        } catch (exception: Exception) {
            log.error("Feilet ved post mot Oppgave (ID: $uuid)", exception)
            tjenestekallLogg.error("""
                Oppgave-error:                 $uuid
                ------------------------------------------------------------------------------------
                    exception:
                    $exception
                ------------------------------------------------------------------------------------
            """.trimIndent())
            return OppgaveResponse()
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
        return prioritet.substringBefore("_", "")
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


package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgave

import com.google.gson.GsonBuilder
import no.nav.common.auth.SsoToken
import no.nav.common.auth.SubjectHandler
import no.nav.common.oidc.SystemUserTokenProvider
import no.nav.log.MDCConstants
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveRequest
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveRestClient
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.oppgave.OppgaveResponse
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.KodeverksmapperService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.domain.Behandling
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.RestConstants
import no.nav.sbl.rest.RestUtils
import no.nav.sbl.util.EnvironmentUtils
import org.slf4j.MDC
import org.joda.time.LocalDate
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.ws.rs.client.Entity
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
        println("fnr til aktor" + oppgave.fnr + getAktørId(oppgave.fnr))
        println("oppgaveobject" + oppgave.toString())


        val oppgaveskjermetObject = OppgaveSkjermetRequestDTO(
                opprettetAvEnhetsnr = oppgave.opprettetavenhetsnummer,
                aktoerId = "1000096233942",//getAktørId(oppgave.fnr),
                behandlesAvApplikasjon = "FS22",
                beskrivelse = oppgave.beskrivelse,
                temagruppe = oppgave.temagruppe,
                tema = oppgave.tema,
                behandlingstema = behandling?.map(Behandling::getBehandlingstema).orElse(null),
                oppgavetype = oppgaveTypeMapped,
                behandlingstype = behandling?.map(Behandling::getBehandlingstype).orElse(null),
                aktivDato = LocalDate.now(),
                fristFerdigstillelse = oppgave.oppgaveFrist,
                prioritet = oppgave.prioritet
        )
        val returobject = gjorSporring(url, oppgaveskjermetObject)
        println("returobject " + returobject)
        return OppgaveResponse()
    }

    private fun gjorSporring(url: String, request: OppgaveSkjermetRequestDTO): OppgaveResponse? {
        val uuid = UUID.randomUUID()
        try {
            println("URL " + url)
            println("entity " + Entity.json(request))
            val ssoToken = SubjectHandler.getSsoToken(SsoToken.Type.OIDC).orElseThrow { RuntimeException("Fant ikke OIDC-token") }
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
                        .header(AUTHORIZATION, RestConstants.AUTH_METHOD_BEARER + RestConstants.AUTH_SEPERATOR + ssoToken)
                        .header(RestConstants.NAV_CONSUMER_TOKEN_HEADER, RestConstants.AUTH_METHOD_BEARER + RestConstants.AUTH_SEPERATOR + consumerOidcToken)
                        .post(Entity.json(request))

                val body = response.readEntity(String::class.java)
                println("body " + body)
                tjenestekallLogg.info("""
                Oppgave-response: $uuid
                ------------------------------------------------------------------------------------
                    status: ${response.status} ${response.statusInfo}
                    body: $body
                ------------------------------------------------------------------------------------
            """.trimIndent())

                body
            }
            println("content " + content)
            return gson.fromJson(content, OppgaveResponse::class.java)
        } catch (exception: Exception) {
            log.error("Feilet ved post mot Oppgave (ID: $uuid)", exception)
            tjenestekallLogg.error("""
                Oppgave-response:                 $uuid
                ------------------------------------------------------------------------------------
                    exception:
                    $exception
                ------------------------------------------------------------------------------------
            """.trimIndent())
            return null
        }


    }


    private fun getAktørId(fnr: String): String? {
        val aktorIdObject = pdlOppslagService.hentIdent(fnr, "AKTORID")
        return aktorIdObject?.data?.get(0)?.ident;
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
        val aktivDato: LocalDate,
        val fristFerdigstillelse: LocalDate?,
        val prioritet: String

)

data class OppaveSkjermetResponsDTO(
//TODO Respons på opprettet oppgave er oppgaveId, trengs egent objekt for skjermetrespons

        val aktoerId: String?,
        val beskrivelse: String?,
        val temagruppe: String?,
        val tema: String?,
        val behandlingstema: String?,
        val oppgavetype: String,
        val behandlingstype: String?,

        val fristFerdigstillelse: LocalDate?,
        val aktivDato: LocalDate,
        val opprettetTidspunkt: LocalDateTime,
        val prioritet: String
)
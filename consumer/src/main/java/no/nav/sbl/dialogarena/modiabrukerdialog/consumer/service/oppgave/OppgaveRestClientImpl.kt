package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgave
import com.google.gson.GsonBuilder
import no.nav.common.auth.SsoToken
import no.nav.common.auth.SubjectHandler
import no.nav.log.MDCConstants
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveRequest
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveResponse
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveRestClient
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.sts.StsServiceImpl
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.RestConstants
import no.nav.sbl.rest.RestUtils.withClient
import no.nav.sbl.util.EnvironmentUtils
import org.slf4j.MDC
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.ws.rs.client.Client
import javax.ws.rs.client.Entity
import javax.ws.rs.core.HttpHeaders.AUTHORIZATION


open class OppgaveOpprettelseClient(private val pdlOppslagService: PdlOppslagService) : OppgaveRestClient {
    private val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()
    val OPPGAVE_BASEURL = EnvironmentUtils.getRequiredProperty("OPPGAVE_BASEURL")

    val url = OPPGAVE_BASEURL + "api/v1/oppgaver"

    @Inject
    private lateinit var stsService: StsServiceImpl

    override fun opprettOppgave(oppgave: OppgaveRequest): OppgaveResponse {
        val oppgaveskjermetObject: OppgaveSkjermetRequestDTO = OppgaveSkjermetRequestDTO(
                opprettetAvEnhetsnr = oppgave.opprettetAvEnhetsnr,
                aktoerId = getAktørId(oppgave.fnr),
                behandlesAvApplikasjon = "FS22",
                beskrivelse = oppgave.beskrivelse,
                temagruppe = oppgave.temagruppe,
                tema = oppgave.tema,
                behandlingstema = "ab0335",
                oppgavetype = oppgave.oppgavetype,
                behandlingstype = "ae0034",
                aktivDato = LocalDate.now(),
                fristFerdigstillelse = LocalDate.now(),
                prioritet = oppgave.prioritet
        )


        return gjorSporring(url, oppgaveskjermetObject, OppgaveResponse::class.java)


    }

    private fun gjorSporring(url: String, request: OppgaveSkjermetRequestDTO, targetClass: Class<OppgaveResponse>): OppgaveResponse {
        val ssoToken = SubjectHandler.getSsoToken(SsoToken.Type.OIDC).orElseThrow { RuntimeException("Fant ikke OIDC-token") }
        return withClient { client: Client ->
            client
                    .target(url)
                    .request()
                    .header(RestConstants.NAV_CALL_ID_HEADER, MDC.get(MDCConstants.MDC_CALL_ID))
                    .header(AUTHORIZATION, "Bearer $ssoToken")
                    .post(Entity.json(request))
                    .readEntity(targetClass)

        }

    }

    private fun getAktørId(fnr: String): String? {
        //TODO :try mangler her
        val aktorIdObject = pdlOppslagService.hentIdent(fnr, "aktorid")

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
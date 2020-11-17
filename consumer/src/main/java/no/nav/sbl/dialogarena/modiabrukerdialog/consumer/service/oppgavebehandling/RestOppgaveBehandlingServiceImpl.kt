package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling

import no.nav.common.auth.subject.SubjectHandler
import no.nav.common.log.MDCConstants
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.EnvironmentUtils
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.apis.OppgaveApi
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.models.GetOppgaveResponseJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.models.PostOppgaveRequestJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.models.PutOppgaveRequestJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentIdent
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.*
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.KodeverksmapperService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.domain.Behandling
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.pdl.PdlSyntetiskMapper
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOppgaveIkkeFunnet
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate
import java.util.*

open class RestOppgaveBehandlingServiceImpl @Autowired constructor(
        val kodeverksmapperService: KodeverksmapperService,
        val pdlOppslagService: PdlOppslagService
) : RestOppgaveBehandlingService{

    @Autowired
    private lateinit var stsService: SystemUserTokenProvider

    val OPPGAVE_BASEURL = EnvironmentUtils.getRequiredProperty("OPPGAVE_BASEURL")
    val apiClient = OppgaveApi(OPPGAVE_BASEURL)
    val consumerOidcToken: String = stsService.systemUserToken

    private val log = LoggerFactory.getLogger(RestOppgaveBehandlingServiceImpl::class.java)


    override fun opprettOppgave(request: OpprettOppgaveRequest): OpprettOppgaveResponse {
        val behandling: Optional<Behandling> = kodeverksmapperService.mapUnderkategori(request.underkategoriKode)
        val oppgaveTypeMapped: String = kodeverksmapperService.mapOppgavetype(request.oppgavetype)
        val aktorId = getAktorId(request.fnr)
        val response = apiClient.opprettOppgave(
                authorization = consumerOidcToken,
                xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                postOppgaveRequestJsonDTO = PostOppgaveRequestJsonDTO(
                        opprettetAvEnhetsnr = request.opprettetavenhetsnummer,
                        aktoerId = aktorId,
                        behandlesAvApplikasjon = "FS22",
                        beskrivelse = request.beskrivelse,
                        temagruppe = request.temagruppe,
                        tema = request.tema,
                        behandlingstema = behandling.map(Behandling::getBehandlingstema).orElse(null),
                        oppgavetype = oppgaveTypeMapped,
                        behandlingstype = behandling.map(Behandling::getBehandlingstype).orElse(null),
                        aktivDato = LocalDate.now(),
                        fristFerdigstillelse = request.oppgaveFrist,
                        prioritet = PostOppgaveRequestJsonDTO.Prioritet.valueOf(stripTemakode(request.prioritet))
                )
        )
        return OpprettOppgaveResponse(response.id?.toString() ?: throw RuntimeException("No oppgaveId found"))
    }

    override fun opprettSkjermetOppgave(request: OpprettOppgaveRequest): OpprettOppgaveResponse {
        val behandling: Optional<Behandling> = kodeverksmapperService.mapUnderkategori(request.underkategoriKode)
        val response = apiClient.opprettOppgave(
                authorization = consumerOidcToken,
                xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                postOppgaveRequestJsonDTO = PostOppgaveRequestJsonDTO(
                        aktoerId = getAktorId(request.fnr),
                        opprettetAvEnhetsnr = request.opprettetavenhetsnummer,
                        behandlesAvApplikasjon = "FS22",
                        beskrivelse = request.beskrivelse,
                        temagruppe = "",
                        tema = request.tema,
                        behandlingstema = behandling.map(Behandling::getBehandlingstema).orElse(null),
                        oppgavetype = kodeverksmapperService.mapOppgavetype(request.oppgavetype),
                        behandlingstype = behandling.map(Behandling::getBehandlingstype).orElse(null),
                        aktivDato = LocalDate.now(),
                        fristFerdigstillelse = request.oppgaveFrist,
                        prioritet = PostOppgaveRequestJsonDTO.Prioritet.valueOf(stripTemakode(request.prioritet))
                )
        )

        return OpprettOppgaveResponse(response.id?.toString() ?: throw java.lang.RuntimeException("No opprageId found"))
    }


    override fun hentOppgave(oppgaveId: String): OppgaveResponse {
        val response = hentOppgaveDTO(oppgaveId)
        return oppgaveToOppgave(response)
    }

    @Throws(RestOppgaveBehandlingService.FikkIkkeTilordnet::class)
    override fun tilordneOppgave(oppgaveId: String, temagruppe: Temagruppe, saksbehandlersValgteEnhet: String) {
        tilordneOppgave(oppgaveId, Optional.ofNullable(temagruppe), saksbehandlersValgteEnhet)
    }

    override fun oppgaveErFerdigstilt(oppgaveid: String): Boolean {
        val response = hentOppgaveDTO(oppgaveid)
        return StringUtils.equalsIgnoreCase(response.status.value, GetOppgaveResponseJsonDTO.Status.FERDIGSTILT.value)
    }


    @Throws(RestOppgaveBehandlingService.FikkIkkeTilordnet::class)
    private fun tilordneOppgave(oppgaveId: String, temagruppe: Optional<Temagruppe>, saksbehandlersValgteEnhet: String) {
        val oppgave = hentOppgaveDTO(oppgaveId)
        val ident: String = SubjectHandler.getIdent().orElseThrow { RuntimeException("Fant ikke ident") }
        try {
            val oppdatertOppgave = oppgave.copy(tilordnetRessurs = ident, tildeltEnhetsnr = saksbehandlersValgteEnhet)
            lagreOppgave(oppdatertOppgave, temagruppe, saksbehandlersValgteEnhet)
        } catch (exeption: Exception) {
            throw RestOppgaveBehandlingService.FikkIkkeTilordnet(exeption)
        }
    }

    private fun hentOppgaveDTO(oppgaveId: String) : GetOppgaveResponseJsonDTO {
        val response = apiClient.hentOppgave(
                xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                id = oppgaveId.toLong()
        )
        return response;
    }

    private fun lagreOppgave(response: GetOppgaveResponseJsonDTO, temagruppe: Optional<Temagruppe>, saksbehandlersValgteEnhet: String) {
        try {
            apiClient.endreOppgave(
                    xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                    id = response.id.toString().toLong(),
                    putOppgaveRequestJsonDTO = PutOppgaveRequestJsonDTO(
                            tildeltEnhetsnr = saksbehandlersValgteEnhet,
                            aktoerId = response.aktoerId,
                            behandlesAvApplikasjon = "FS22",
                            beskrivelse = response.beskrivelse,
                            temagruppe = temagruppe.toString(),
                            tema = response.tema,
                            behandlingstema = response.behandlingstema,
                            oppgavetype = response.oppgavetype,
                            behandlingstype = response.behandlingstype,
                            aktivDato = response.aktivDato,
                            fristFerdigstillelse = response.fristFerdigstillelse,
                            prioritet = PutOppgaveRequestJsonDTO.Prioritet.valueOf(stripTemakode(response.prioritet.toString())),
                            endretAvEnhetsnr = response.endretAvEnhetsnr,
                            status = PutOppgaveRequestJsonDTO.Status.AAPNET,
                            versjon = 1,
                            tilordnetRessurs = response.tilordnetRessurs
                    )
            )
        } catch (e: LagreOppgaveOppgaveIkkeFunnet) {
            log.info("Oppgaven ble ikke funnet ved tilordning til saksbehandler. Oppgaveid: " + response.id, e)
            throw RuntimeException("Oppgaven ble ikke funnet ved tilordning til saksbehandler", e)
        }
    }

    private fun getAktorId(fnr: String): String? {
        return try {
            pdlOppslagService
                    .hentIdent(fnr)
                    ?.identer
                    ?.find { ident -> ident.gruppe == HentIdent.IdentGruppe.AKTORID }
                    ?.ident
                    // syntmapping for Q2 --> Q1
                    ?.let(PdlSyntetiskMapper::mapAktorIdFraPdl)
        } catch (exception: Exception) {
            null
        }
    }

    private fun oppgaveToOppgave(response: GetOppgaveResponseJsonDTO): OppgaveResponse {
        val erSTO = Optional
                .ofNullable(response.oppgavetype)
                .map { kodeverksmapperService.mapOppgavetype(response.oppgavetype) }
                .map { anObject: String? -> "SPM_OG_SVR" == anObject }
                .orElse(false)
        return OppgaveResponse(
                response.id.toString(),
                response.aktoerId.toString(),
                response.journalpostId.toString(),
                erSTO
        )
    }

    private fun stripTemakode(prioritet: String): String {
        return prioritet.substringBefore("_")
    }
}

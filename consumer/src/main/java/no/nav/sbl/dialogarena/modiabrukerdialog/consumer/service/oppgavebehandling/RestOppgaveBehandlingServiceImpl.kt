package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling

import no.nav.common.auth.subject.SubjectHandler
import no.nav.common.log.MDCConstants
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.EnvironmentUtils
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.apis.OppgaveApi
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.models.GetOppgaveResponseJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.models.OppgaveJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.models.PostOppgaveRequestJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.models.PutOppgaveRequestJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentIdent
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveResponse
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OpprettOppgaveRequest
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OpprettOppgaveResponse
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.RestOppgaveBehandlingService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.KodeverksmapperService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.domain.Behandling
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.pdl.PdlSyntetiskMapper
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSOppgave
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSHentOppgaveResponse
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOppgaveIkkeFunnet
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOptimistiskLasing
import no.nav.tjeneste.virksomhet.tildeloppgave.v1.WSTildelFlereOppgaverRequest
import no.nav.tjeneste.virksomhet.tildeloppgave.v1.WSTildelFlereOppgaverResponse
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Collectors

open class RestOppgaveBehandlingServiceImpl @Autowired constructor(
        val kodeverksmapperService: KodeverksmapperService,
        val pdlOppslagService: PdlOppslagService,
        val tilgangskontroll: Tilgangskontroll
) : RestOppgaveBehandlingService{
    val SPORSMAL_OG_SVAR = "SPM_OG_SVR"
    val KONTAKT_NAV = "KNA"

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


    override fun hentOppgave(id: String): OppgaveResponse {
        val response = apiClient.hentOppgave(
                xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                id = id.toLong()
        )

        return oppgaveToOppgave(response)
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

    private fun oppgaveToOppgave(oppgave: OppgaveJsonDTO): OppgaveResponse {
        val erSTO = Optional
                .ofNullable(oppgave.oppgavetype)
                .map { kodeverksmapperService.mapOppgavetype(oppgave.oppgavetype) }
                .map { anObject: String? -> "SPM_OG_SVR" == anObject }
                .orElse(false)
        return OppgaveResponse(
                oppgave.id.toString(),
                oppgave.aktoerId.toString(),
                oppgave.journalpostId.toString(),
                erSTO
        )
    }

    @Throws(RestOppgaveBehandlingService.FikkIkkeTilordnet::class)
    override fun tilordneOppgave(oppgaveId: String, temagruppe: Temagruppe, saksbehandlersValgteEnhet: String) {
        tilordneOppgave(oppgaveId, Optional.ofNullable(temagruppe), saksbehandlersValgteEnhet)
    }

    @Throws(RestOppgaveBehandlingService.FikkIkkeTilordnet::class)
    private fun tilordneOppgave(oppgaveId: String, temagruppe: Optional<Temagruppe>, saksbehandlersValgteEnhet: String) {
        val response = apiClient.hentOppgave(
                xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                id = oppgaveId.toLong()
        )
        tilordneOppgave(response, temagruppe, saksbehandlersValgteEnhet)
    }

    @Throws(RestOppgaveBehandlingService.FikkIkkeTilordnet::class)
    private fun tilordneOppgave(response: GetOppgaveResponseJsonDTO, temagruppe: Optional<Temagruppe>, saksbehandlersValgteEnhet: String): GetOppgaveResponseJsonDTO {
        try {
            val oppgaveRespons = apiClient.hentOppgave(
                    xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                    id = response.id.toString().toLong()
            )
            lagreOppgave(oppgaveRespons, temagruppe, saksbehandlersValgteEnhet)
            return response
        } catch (exeption: Exception) {
            throw RestOppgaveBehandlingService.FikkIkkeTilordnet(exeption)
        }
    }

    open fun lagreOppgave(response: GetOppgaveResponseJsonDTO, temagruppe: Temagruppe, saksbehandlersValgteEnhet: String) {
        lagreOppgave(response, Optional.ofNullable(temagruppe), saksbehandlersValgteEnhet)
    }

    private fun lagreOppgave(respons: GetOppgaveResponseJsonDTO, temagruppe: Optional<Temagruppe>, saksbehandlersValgteEnhet: String) {
        try {
            apiClient.endreOppgave(
                    xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                    id = respons.id.toString().toLong(),
                    putOppgaveRequestJsonDTO = PutOppgaveRequestJsonDTO(
                            tildeltEnhetsnr = respons.tildeltEnhetsnr,
                            aktoerId = respons.aktoerId,
                            behandlesAvApplikasjon = "FS22",
                            beskrivelse = respons.beskrivelse,
                            temagruppe = respons.temagruppe,
                            tema = respons.tema,
                            behandlingstema = respons.behandlingstema,
                            oppgavetype = respons.oppgavetype,
                            behandlingstype = respons.behandlingstype,
                            aktivDato = respons.aktivDato,
                            fristFerdigstillelse = respons.fristFerdigstillelse,
                            prioritet = PutOppgaveRequestJsonDTO.Prioritet.valueOf(stripTemakode(respons.prioritet.toString())),
                            endretAvEnhetsnr = respons.endretAvEnhetsnr,
                            status = PutOppgaveRequestJsonDTO.Status.AAPNET,
                            versjon = 1
                    )
            )
        } catch (e: LagreOppgaveOppgaveIkkeFunnet) {
            TODO("Må endre catch")
            log.info("Oppgaven ble ikke funnet ved tilordning til saksbehandler. Oppgaveid: " + respons.id, e)
            throw RuntimeException("Oppgaven ble ikke funnet ved tilordning til saksbehandler", e)
        }
    }

    override fun finnTildelteOppgaver(): List<OppgaveResponse> {
        val ident: String = SubjectHandler.getIdent().orElseThrow { RuntimeException("Fant ikke ident") }
        val aktivStatus = "AAPEN"
        val response = apiClient.finnOppgaver(
                xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                statuskategori = aktivStatus,
                tema = listOf(KONTAKT_NAV),
                oppgavetype = listOf(SPORSMAL_OG_SVAR),
                tilordnetRessurs = ident
        ).oppgaver!!.stream()
                .map { oppgave: OppgaveJsonDTO -> oppgaveToOppgave(oppgave) }
                .collect(Collectors.toList())

        return validerTilgangTilbruker(response)
    }

    private fun validerTilgangTilbruker(oppgaveList: List<OppgaveResponse>): List<OppgaveResponse> {
        if (oppgaveList.isEmpty()) {
            return emptyList()
        } else if (tilgangskontroll
                        .check(Policies.tilgangTilBruker.with(oppgaveList[0].fnr))
                        .getDecision()
                        .isPermit()) {
            return oppgaveList
        }
        oppgaveList.forEach(Consumer { enkeltoppgave: OppgaveResponse -> systemLeggTilbakeOppgave(enkeltoppgave.oppgaveId, null, "4100") })
        return emptyList()
    }

    override fun plukkOppgaver(temagruppe: Temagruppe, saksbehandlersValgteEnhet: String): List<OppgaveResponse> {
        val enhetsId = saksbehandlersValgteEnhet.toInt()
        return tildelEldsteLedigeOppgaver(temagruppe, enhetsId, saksbehandlersValgteEnhet).stream()
                .map { oppgave: OppgaveJsonDTO -> oppgaveToOppgave(oppgave) }
                .collect(Collectors.toList())
    }

    private fun tildelEldsteLedigeOppgaver(temagruppe: Temagruppe, enhetsId: Int, saksbehandlersValgteEnhet: String): List<OppgaveJsonDTO> {
        TODO("Må endres til å bruke OppgaveApi")
        val ident = SubjectHandler.getIdent().orElseThrow { RuntimeException("Fant ikke ident") }
        val response: WSTildelFlereOppgaverResponse = tildelOppgaveWS.tildelFlereOppgaver(
                WSTildelFlereOppgaverRequest()
                        .withUnderkategori(OppgaveBehandlingServiceImpl.underkategoriKode(temagruppe))
                        .withOppgavetype(OppgaveBehandlingServiceImpl.SPORSMAL_OG_SVAR)
                        .withFagomrade(OppgaveBehandlingServiceImpl.KONTAKT_NAV)
                        .withAnsvarligEnhetId(enhetFor(temagruppe, saksbehandlersValgteEnhet))
                        .withIkkeTidligereTildeltSaksbehandlerId(ident)
                        .withTildeltAvEnhetId(enhetsId)
                        .withTildelesSaksbehandlerId(ident))
                ?: return emptyList()
        return response.oppgaveIder.stream()
                .map(Function<Int, WSHentOppgaveResponse> { oppgaveId: Int? -> this.hentOppgaveResponseFraGsak(oppgaveId) })
                .filter { obj: WSHentOppgaveResponse? -> Objects.nonNull(obj) }
                .map { obj: WSHentOppgaveResponse -> obj.oppgave }
                .collect(Collectors.toList())
    }

    override fun systemLeggTilbakeOppgave(oppgaveId: String, temagruppe: Temagruppe, saksbehandlersValgteEnhet: String) {
        try {
            val response = apiClient.hentOppgave(
                    xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                    id = oppgaveId.toLong()
            )
            lagreOppgave(response, temagruppe, saksbehandlersValgteEnhet)
        } catch (lagreOppgaveOptimistiskLasing: LagreOppgaveOptimistiskLasing) {
            TODO("Må endre catch")
            throw RuntimeException("Oppgaven kunne ikke lagres, den er for øyeblikket låst av en annen bruker.", lagreOppgaveOptimistiskLasing)
        }
    }

    override fun oppgaveErFerdigstilt(oppgaveid: String): Boolean {

        val response = apiClient.hentOppgave(
                xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                id = oppgaveid.toLong()
        )

        return StringUtils.equalsIgnoreCase(response.status.value, GetOppgaveResponseJsonDTO.Status.FERDIGSTILT.value)
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

    private fun stripTemakode(prioritet: String): String {
        return prioritet.substringBefore("_")
    }
}

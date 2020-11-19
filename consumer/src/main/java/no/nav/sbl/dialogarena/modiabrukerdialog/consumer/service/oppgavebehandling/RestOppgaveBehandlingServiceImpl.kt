package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling

import no.nav.common.auth.subject.SubjectHandler
import no.nav.common.log.MDCConstants
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.EnvironmentUtils
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.apis.OppgaveApi
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.models.*
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentIdent
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.*
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.KodeverksmapperService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.domain.Behandling
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.pdl.PdlSyntetiskMapper
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.tjeneste.virksomhet.oppgave.v3.HentOppgaveOppgaveIkkeFunnet
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOppgaveIkkeFunnet
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOptimistiskLasing
import no.nav.tjeneste.virksomhet.tildeloppgave.v1.WSTildelFlereOppgaverRequest
import no.nav.tjeneste.virksomhet.tildeloppgave.v1.WSTildelFlereOppgaverResponse
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
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
        val tilgangskontroll: Tilgangskontroll,
        val ansattService: AnsattService,
        val leggTilbakeOppgaveDelegate: LeggTilbakeOppgaveDelegate
) : RestOppgaveBehandlingService{
    val SPORSMAL_OG_SVAR = "SPM_OG_SVR"
    val KONTAKT_NAV = "KNA"

    @Autowired
    private lateinit var stsService: SystemUserTokenProvider

    val OPPGAVE_BASEURL = EnvironmentUtils.getRequiredProperty("OPPGAVE_BASEURL")
    val apiClient = OppgaveApi(OPPGAVE_BASEURL)
    val consumerOidcToken: String = stsService.systemUserToken
    val DEFAULT_ENHET = 4100
    val STORD_ENHET = "4842"

    private val log = LoggerFactory.getLogger(RestOppgaveBehandlingServiceImpl::class.java)


    override fun opprettOppgave(request: OpprettOppgaveRequest): OpprettOppgaveResponse {
        val behandling: Optional<Behandling> = kodeverksmapperService.mapUnderkategori(request.underkategoriKode)
        val oppgaveTypeMapped: String = kodeverksmapperService.mapOppgavetype(request.oppgavetype)
        val aktorId = getAktorId(request.fnr)
        val response = apiClient.opprettOppgave(
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
        return StringUtils.equalsIgnoreCase(response.status.value, OppgaveJsonDTO.Status.FERDIGSTILT.value)
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

    private fun hentOppgaveDTO(oppgaveId: String) : OppgaveJsonDTO {
        val response = apiClient.hentOppgave(
                xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                id = oppgaveId.toLong()
        )
        return response.fromDTO()
    }

    private fun endreOppgaveDTO(response: OppgaveJsonDTO, temagruppe: Optional<Temagruppe>, saksbehandlersValgteEnhet: String) : OppgaveJsonDTO {
        val oppgave = apiClient.endreOppgave(
                xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                id = response.id.toString().toLong(),
                putOppgaveRequestJsonDTO = PutOppgaveRequestJsonDTO(
                        id = response.id,
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
                        endretAvEnhetsnr = enhetFor(temagruppe, saksbehandlersValgteEnhet),
                        status = PutOppgaveRequestJsonDTO.Status.AAPNET,
                        versjon = 1,
                        tilordnetRessurs = response.tilordnetRessurs
                )
        )
        return oppgave.fromDTO();
    }

    fun lagreOppgave(response: OppgaveJsonDTO, temagruppe: Temagruppe, saksbehandlersValgteEnhet: String) {
        lagreOppgave(response, Optional.ofNullable(temagruppe), saksbehandlersValgteEnhet)
    }

    fun lagreOppgave(response: OppgaveJsonDTO, temagruppe: Optional<Temagruppe>, saksbehandlersValgteEnhet: String) {
        try {
            endreOppgaveDTO(response, temagruppe, saksbehandlersValgteEnhet)
        } catch (e: LagreOppgaveOppgaveIkkeFunnet) {
            log.info("Oppgaven ble ikke funnet ved tilordning til saksbehandler. Oppgaveid: " + response.id, e)
            TODO("Må endre catch")
            log.info("Oppgaven ble ikke funnet ved tilordning til saksbehandler. Oppgaveid: " + response.id, e)
            throw RuntimeException("Oppgaven ble ikke funnet ved tilordning til saksbehandler", e)
        }
    }

    private fun enhetFor(temagruppe: Temagruppe, saksbehandlersValgteEnhet: String): String {
        return enhetFor(Optional.ofNullable(temagruppe), saksbehandlersValgteEnhet)
    }

    private fun enhetFor(optional: Optional<Temagruppe>, saksbehandlersValgteEnhet: String): String {
        if (!optional.isPresent) {
            return DEFAULT_ENHET.toString()
        }
        val temagruppe = optional.get()
        return if (temagruppe == Temagruppe.FMLI && saksbehandlersValgteEnhet == STORD_ENHET) {
            STORD_ENHET
        } else if (listOf(Temagruppe.ARBD, Temagruppe.HELSE, Temagruppe.FMLI, Temagruppe.FDAG, Temagruppe.ORT_HJE, Temagruppe.PENS, Temagruppe.UFRT, Temagruppe.PLEIEPENGERSY, Temagruppe.UTLAND).contains(temagruppe)) {
            DEFAULT_ENHET.toString()
        } else {
            saksbehandlersValgteEnhet
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

    private fun hentOppgaveResponse(oppgaveId: Int): GetOppgaveResponseJsonDTO {
        return hentOppgaveResponse(oppgaveId.toString())
    }

    private fun hentOppgaveResponse(oppgaveId: String): GetOppgaveResponseJsonDTO {
        return try {
            apiClient.hentOppgave(
                    xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                    id = oppgaveId.toLong()
            )
        } catch (exc: HentOppgaveOppgaveIkkeFunnet) {
            throw RuntimeException("HentOppgaveOppgaveIkkeFunnet", exc)
        }
    }

    override fun plukkOppgaver(temagruppe: Temagruppe, saksbehandlersValgteEnhet: String): List<OppgaveResponse> {
        val enhetsId = saksbehandlersValgteEnhet.toInt()
        return tildelEldsteLedigeOppgaver(temagruppe, enhetsId, saksbehandlersValgteEnhet).stream()
                .map { oppgave: OppgaveJsonDTO -> oppgaveToOppgave(oppgave) }
                .collect(Collectors.toList())
    }

    private fun tildelEldsteLedigeOppgaver(temagruppe: Temagruppe, enhetsId: Int, saksbehandlersValgteEnhet: String): List<OppgaveJsonDTO> {
        val ident = SubjectHandler.getIdent().orElseThrow { RuntimeException("Fant ikke ident") }
        TODO("Endre til å bruke oppgaveApi")
        val response: WSTildelFlereOppgaverResponse = tildelOppgaveWS.tildelFlereOppgaver(
                WSTildelFlereOppgaverRequest()
                        .withUnderkategori(OppgaveBehandlingServiceImpl.underkategoriKode(temagruppe))
                        .withOppgavetype(OppgaveBehandlingServiceImpl.SPORSMAL_OG_SVAR)
                        .withFagomrade(OppgaveBehandlingServiceImpl.KONTAKT_NAV)
                        .withAnsvarligEnhetId(enhetFor(temagruppe, saksbehandlersValgteEnhet))
                        .withIkkeTidligereTildeltSaksbehandlerId(ident)
                        .withTildeltAvEnhetId(enhetsId)
                        .withTildelesSaksbehandlerId(ident))
        if (response.oppgaveIder.isNullOrEmpty()) {
            return emptyList();
        }
        return response.oppgaveIder.stream()
                .map { oppgacveId: Int -> hentOppgaveResponse(oppgacveId) }
                .filter { obj: GetOppgaveResponseJsonDTO -> Objects.nonNull(obj) }
                .map { obj: GetOppgaveResponseJsonDTO -> oppgaveToOppgave(hentOppgaveDTO(obj.id.toString())) }
                .collect(Collectors.toList())
    }

    override fun ferdigstillOppgave(oppgaveId: String, temagruppe: Temagruppe, saksbehandlersValgteEnhet: String) {
        ferdigstillOppgave(oppgaveId, Optional.ofNullable(temagruppe), saksbehandlersValgteEnhet)
    }

    override fun ferdigstillOppgave(oppgaveId: String, temagruppe: Optional<Temagruppe>, saksbehandlersValgteEnhet: String) {
        ferdigstillOppgaver(listOf(oppgaveId), temagruppe, saksbehandlersValgteEnhet)
    }

    override fun ferdigstillOppgaver(oppgaveIder: List<String>, temagruppe: Optional<Temagruppe>, saksbehandlersValgteEnhet: String) {
        val patchJsonDTOListe = mutableListOf<PatchJsonDTO>()
        for (oppgaveId in oppgaveIder) {
            oppdaterBeskrivelse(temagruppe, saksbehandlersValgteEnhet, oppgaveId)
            patchJsonDTOListe += PatchJsonDTO(1, oppgaveId.toLong())
        }
        try {
            apiClient.patchOppgaver(
                    xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                    patchOppgaverRequestJsonDTO = PatchOppgaverRequestJsonDTO(
                            oppgaver = patchJsonDTOListe,
                            status = PatchOppgaverRequestJsonDTO.Status.FERDIGSTILT,
                            tilordnetRessurs = enhetFor(temagruppe, saksbehandlersValgteEnhet)
                    )
            )
            log.info("Forsøker å ferdigstille oppgave med oppgaveIder" + oppgaveIder + "for enhet" + saksbehandlersValgteEnhet)
        } catch (e: java.lang.Exception) {
            val ider = java.lang.String.join(", ", oppgaveIder)
            log.warn("Ferdigstilling av oppgavebolk med oppgaveider: $ider, med enhet $saksbehandlersValgteEnhet feilet.", e)
            throw e
        }
    }

    override fun ferdigstillOppgave(oppgaveId: String, temagruppe: Optional<Temagruppe>, saksbehandlersValgteEnhet: String, beskrivelse: String) {
        oppdaterBeskrivelse(temagruppe, saksbehandlersValgteEnhet, oppgaveId, beskrivelse)
        try {
            apiClient.patchOppgave(
                    xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                    id = oppgaveId.toLong(),
                    patchOppgaveRequestJsonDTO = PatchOppgaveRequestJsonDTO(
                            versjon = 1,
                            id = oppgaveId.toLong(),
                            status = PatchOppgaveRequestJsonDTO.Status.FERDIGSTILT,
                            tilordnetRessurs = enhetFor(temagruppe, saksbehandlersValgteEnhet)
                    )
            )
            log.info("Forsøker å ferdigstille oppgave med oppgaveId" + oppgaveId + "for enhet" + saksbehandlersValgteEnhet)
        } catch (e: java.lang.Exception) {
            log.error("Kunne ikke ferdigstille oppgave i Modia med oppgaveId $oppgaveId", e)
            throw e
        }
    }

    override fun leggTilbakeOppgave(request: LeggTilbakeOppgaveRequest) {
        if (request.oppgaveId.isNullOrEmpty() || request.beskrivelse.isNullOrEmpty()) {
            return
        }
        val oppgave = hentOppgaveDTO(request.oppgaveId)
        leggTilbakeOppgaveDelegate.leggTilbake(oppgave, request)
    }

     fun leggTilBeskrivelse(gammelBeskrivelse: String?, leggTil: String, valgtEnhet: String): String {
        val ident = SubjectHandler.getIdent().orElseThrow { RuntimeException("Fant ikke ident") }
        val header = String.format("--- %s %s (%s, %s) ---\n",
                DateTimeFormat.forPattern("dd.MM.yyyy HH:mm").print(DateTime.now()),
                ansattService.hentAnsattNavn(ident),
                ident,
                valgtEnhet)
        val nyBeskrivelse = header + leggTil
        return if (StringUtils.isBlank(gammelBeskrivelse)) nyBeskrivelse else nyBeskrivelse + "\n\n" + gammelBeskrivelse
    }

    private fun oppdaterBeskrivelse(temagruppe: Optional<Temagruppe>, saksbehandlersValgteEnhet: String, oppgaveId: String, beskrivelse: String) {
        try {
            val oppgave = hentOppgaveDTO(oppgaveId)
            val nyBeskrivelse = leggTilBeskrivelse(oppgave.beskrivelse, "Oppgaven er ferdigstilt i Modia. " + beskrivelse, saksbehandlersValgteEnhet)
            val oppdatertOppgave = oppgave.copy(beskrivelse = nyBeskrivelse)
            val endretOppgave = endreOppgaveDTO(oppdatertOppgave, temagruppe, saksbehandlersValgteEnhet)

            lagreOppgave(endretOppgave, temagruppe, saksbehandlersValgteEnhet)
        } catch (e: HentOppgaveOppgaveIkkeFunnet) {
            log.info("Feil ved oppdatering av beskrivelse for oppgave $oppgaveId", e)
            throw RuntimeException(e)
        } catch (e: LagreOppgaveOptimistiskLasing) {
            log.info("Feil ved oppdatering av beskrivelse for oppgave $oppgaveId", e)
            throw RuntimeException(e)
        }
    }

    private fun oppdaterBeskrivelse(temagruppe: Optional<Temagruppe>, saksbehandlersValgteEnhet: String, oppgaveId: String) {
        oppdaterBeskrivelse(temagruppe, saksbehandlersValgteEnhet, oppgaveId, "")
    }

    override fun systemLeggTilbakeOppgave(oppgaveId: String, temagruppe: Temagruppe, saksbehandlersValgteEnhet: String) {
        try {
            val oppgave = hentOppgaveDTO(oppgaveId)
            lagreOppgave(oppgave, temagruppe, saksbehandlersValgteEnhet)
        } catch (lagreOppgaveOptimistiskLasing: LagreOppgaveOptimistiskLasing) {
            TODO("Må endre catch")
            throw RuntimeException("Oppgaven kunne ikke lagres, den er for øyeblikket låst av en annen bruker.", lagreOppgaveOptimistiskLasing)
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

    private fun oppgaveToOppgave(response: OppgaveJsonDTO): OppgaveResponse {
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

    fun GetOppgaveResponseJsonDTO.fromDTO() : OppgaveJsonDTO = TODO()
    fun PutOppgaveResponseJsonDTO.fromDTO() : OppgaveJsonDTO = TODO()

    private fun stripTemakode(prioritet: String): String {
        return prioritet.substringBefore("_")
    }
}

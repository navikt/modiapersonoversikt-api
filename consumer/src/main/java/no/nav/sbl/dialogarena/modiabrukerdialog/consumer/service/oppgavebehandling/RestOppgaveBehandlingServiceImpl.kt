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
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate
import java.util.*


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
        if (aktorId == null || aktorId.isEmpty()) {
            throw Exception("AktørId-mangler på person")
        }
        val response = apiClient.opprettOppgave(
                authorization = consumerOidcToken,
                xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                postOppgaveRequestJsonDTO = PostOppgaveRequestJsonDTO(
                        opprettetAvEnhetsnr = request.opprettetavenhetsnummer,
                        aktoerId = aktorId,
                        behandlesAvApplikasjon = "FS22",
                        tilordnetRessurs = request.ansvarligIdent,
                        tildeltEnhetsnr = request.ansvarligEnhetId,
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
        val oppgaveTypeMapped: String = kodeverksmapperService.mapOppgavetype(request.oppgavetype)
        val aktorId = getAktorId(request.fnr)
        if (aktorId == null || aktorId.isEmpty()) {
            throw Exception("AktørId-mangler på person")
        }
        val response = apiClient.opprettOppgave(
                authorization = consumerOidcToken,
                xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                postOppgaveRequestJsonDTO = PostOppgaveRequestJsonDTO(
                        aktoerId = aktorId,
                        opprettetAvEnhetsnr = request.opprettetavenhetsnummer,
                        behandlesAvApplikasjon = "FS22",
                        beskrivelse = request.beskrivelse,
                        temagruppe = "",
                        tema = request.tema,
                        behandlingstema = behandling.map(Behandling::getBehandlingstema).orElse(null),
                        oppgavetype = oppgaveTypeMapped,
                        behandlingstype = behandling.map(Behandling::getBehandlingstype).orElse(null),
                        aktivDato = LocalDate.now(),
                        fristFerdigstillelse = request.oppgaveFrist,
                        prioritet = PostOppgaveRequestJsonDTO.Prioritet.valueOf(stripTemakode(request.prioritet))
                )
        )

        return OpprettOppgaveResponse(response.id?.toString() ?: throw java.lang.RuntimeException("No oppgaveId found"))
    }

    override fun hentOppgave(oppgaveId: String): OppgaveResponse {
        val response = hentOppgaveDTO(oppgaveId)
        return oppgaveJsonDTOToOppgaveResponse(response)
    }

    @Throws(RestOppgaveBehandlingService.FikkIkkeTilordnet::class)
    override fun tilordneOppgave(oppgaveId: String, temagruppe: Temagruppe, saksbehandlersValgteEnhet: String) {
        val oppgave = hentOppgaveDTO(oppgaveId)
        val ident: String = SubjectHandler.getIdent().orElseThrow { RuntimeException("Fant ikke ident") }
        try {
            val oppdatertOppgave = oppgave.copy(
                    tilordnetRessurs = ident
            )
            lagreOppgave(oppdatertOppgave, temagruppe, saksbehandlersValgteEnhet)
        } catch (e: Exception) {
            throw RestOppgaveBehandlingService.FikkIkkeTilordnet(e)
        }
    }

    override fun oppgaveErFerdigstilt(oppgaveid: String): Boolean {
        val response = hentOppgaveDTO(oppgaveid)
        return StringUtils.equalsIgnoreCase(response.status.value, OppgaveJsonDTO.Status.FERDIGSTILT.value)
    }

    override fun finnTildelteOppgaver(): List<OppgaveResponse> {
        val ident: String = SubjectHandler.getIdent().orElseThrow { RuntimeException("Fant ikke ident") }
        val aktivStatus = "AAPEN"
        val response = apiClient.finnOppgaver(
                xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                statuskategori = aktivStatus,
                status = null,
                tema = listOf(KONTAKT_NAV),
                oppgavetype = listOf(SPORSMAL_OG_SVAR),
                tildeltEnhetsnr = null,
                tildeltRessurs = null,
                tilordnetRessurs = ident,
                behandlingstema = null,
                behandlingstype = null,
                erUtenMappe = null,
                aktoerId = null,
                journalpostId = null,
                saksreferanse = null,
                opprettetAv = null,
                opprettetAvEnhetsnr = null,
                aktivDatoFom = null,
                aktivDatoTom = null,
                opprettetFom = null,
                opprettetTom = null,
                ferdigstiltFom = null,
                ferdigstiltTom = null,
                fristFom = null,
                fristTom = null,
                orgnr = null,
                sorteringsfelt = null,
                limit = null,
                offset = null
        )
        val tildelteOppgaver = response.oppgaver!!.map { oppgave: OppgaveJsonDTO -> oppgaveJsonDTOToOppgaveResponse(oppgave) }
        return finnOppgaverMedTilgang(tildelteOppgaver)
    }

    override fun plukkOppgaver(temagruppe: Temagruppe, saksbehandlersValgteEnhet: String): List<OppgaveResponse> {
        TODO("Må gjøre plukkOppgaver(...)")
    }

    override fun ferdigstillOppgave(oppgaveId: String, temagruppe: Temagruppe, saksbehandlersValgteEnhet: String) {
        ferdigstillOppgaver(listOf(oppgaveId), temagruppe, saksbehandlersValgteEnhet)
    }

    override fun ferdigstillOppgave(oppgaveId: String, temagruppe: Temagruppe, saksbehandlersValgteEnhet: String, beskrivelse: String) {
        val oppgave = hentOppgaveDTO(oppgaveId)
        val oppgaveOppdatert = oppgave.copy(
                beskrivelse = formatterBeskrivelseFerdigstiltOppgave(
                        saksbehandlersValgteEnhet,
                        oppgave.beskrivelse,
                        beskrivelse
                )
        )
        lagreOppgave(oppgaveOppdatert, temagruppe, saksbehandlersValgteEnhet)
        try {
            apiClient.patchOppgave(
                    xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                    id = oppgaveId.toLong(),
                    patchOppgaveRequestJsonDTO = PatchOppgaveRequestJsonDTO(
                            id = oppgaveId.toLong(),
                            versjon = oppgaveOppdatert.versjon,
                            status = PatchOppgaveRequestJsonDTO.Status.FERDIGSTILT,
                            endretAvEnhetsnr = enhetFor(temagruppe, saksbehandlersValgteEnhet)
                    )
            )
            log.info("Forsøker å ferdigstille oppgave med oppgaveId" + oppgaveId + "for enhet" + saksbehandlersValgteEnhet)
        } catch (e: Exception) {
            log.error("Kunne ikke ferdigstille oppgave i Modia med oppgaveId $oppgaveId", e)
            throw e
        }
    }

    override fun ferdigstillOppgaver(oppgaveIder: List<String>, temagruppe: Temagruppe, saksbehandlersValgteEnhet: String) {
        val patchJsonDTOListe = mutableListOf<PatchJsonDTO>()
        for (oppgaveId in oppgaveIder) {
            val oppgave = hentOppgaveDTO(oppgaveId)
            val oppgaveOppdatert = oppgave.copy(
                    beskrivelse = formatterBeskrivelseFerdigstiltOppgave(
                            saksbehandlersValgteEnhet,
                            oppgave.beskrivelse
                    )
            )
            lagreOppgave(oppgaveOppdatert, temagruppe, saksbehandlersValgteEnhet)
            patchJsonDTOListe += PatchJsonDTO(oppgaveOppdatert.versjon, oppgaveId.toLong())
        }
        try {
            apiClient.patchOppgaver(
                    xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                    patchOppgaverRequestJsonDTO = PatchOppgaverRequestJsonDTO(
                            oppgaver = patchJsonDTOListe,
                            status = PatchOppgaverRequestJsonDTO.Status.FERDIGSTILT,
                            endretAvEnhetsnr = enhetFor(temagruppe, saksbehandlersValgteEnhet)
                    )
            )
            log.info("Forsøker å ferdigstille oppgave med oppgaveIder" + oppgaveIder + "for enhet" + saksbehandlersValgteEnhet)
        } catch (e: Exception) {
            val ider = java.lang.String.join(", ", oppgaveIder)
            log.warn("Ferdigstilling av oppgavebolk med oppgaveider: $ider, med enhet $saksbehandlersValgteEnhet feilet.", e)
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

    private fun endreOppgave(request: OppgaveJsonDTO) : PutOppgaveResponseJsonDTO {
        val oppgave = apiClient.endreOppgave(
                xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                id = request.id!!.toLong(),
                putOppgaveRequestJsonDTO = PutOppgaveRequestJsonDTO(
                        id = request.id,
                        tildeltEnhetsnr = request.tildeltEnhetsnr,
                        aktoerId = request.aktoerId,
                        behandlesAvApplikasjon = "FS22",
                        beskrivelse = request.beskrivelse,
                        temagruppe = request.temagruppe,
                        tema = request.tema,
                        behandlingstema = request.behandlingstema,
                        oppgavetype = request.oppgavetype,
                        behandlingstype = request.behandlingstype,
                        aktivDato = request.aktivDato,
                        fristFerdigstillelse = request.fristFerdigstillelse,
                        prioritet = PutOppgaveRequestJsonDTO.Prioritet.valueOf(request.prioritet.value),
                        endretAvEnhetsnr = request.endretAvEnhetsnr,
                        status = PutOppgaveRequestJsonDTO.Status.AAPNET,
                        versjon = request.versjon + 1,
                        tilordnetRessurs = request.tilordnetRessurs
                )
        )
        return oppgave
    }

    fun lagreOppgave(request: OppgaveJsonDTO, temagruppe: Temagruppe, saksbehandlersValgteEnhet: String) {
        try {
            val oppgave = request.copy(
                    endretAvEnhetsnr = enhetFor(temagruppe, saksbehandlersValgteEnhet)
            )
            endreOppgave(oppgave)
        } catch (e: Exception) {
            log.info("Oppgaven ble ikke funnet ved tilordning til saksbehandler. Oppgaveid: " + request.id, e)
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

    private fun finnOppgaverMedTilgang(oppgaveList: List<OppgaveResponse>): List<OppgaveResponse> {
        if (oppgaveList.isEmpty()) {
            return emptyList()
        } else if (tilgangskontroll
                        .check(Policies.tilgangTilBruker.with(oppgaveList[0].fnr))
                        .getDecision()
                        .isPermit()) {
            return oppgaveList
        }
        oppgaveList.forEach { oppgave: OppgaveResponse -> systemLeggTilbakeOppgave(oppgave.oppgaveId, Temagruppe.valueOf(null.toString()), "4100") }
        return emptyList()
    }

    private fun hentOppgaveDTO(oppgaveId: String) : OppgaveJsonDTO {
        val response = apiClient.hentOppgave(
                xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                id = oppgaveId.toLong()
        )
        return response.toOppgaveJsonDTO()
    }

    private fun formatterBeskrivelseFerdigstiltOppgave(saksbehandlersValgteEnhet: String, gammelBeskrivelse: String?, beskrivelse: String) : String {
        return leggTilBeskrivelse(gammelBeskrivelse, "Oppgaven er ferdigstilt i Modia. " + beskrivelse, saksbehandlersValgteEnhet)
    }

    private fun formatterBeskrivelseFerdigstiltOppgave(saksbehandlersValgteEnhet: String, gammelBeskrivelse: String?) : String{
        return leggTilBeskrivelse(gammelBeskrivelse, "", saksbehandlersValgteEnhet)
    }

    override fun systemLeggTilbakeOppgave(oppgaveId: String, temagruppe: Temagruppe, saksbehandlersValgteEnhet: String) {
        try {
            val oppgave = hentOppgaveDTO(oppgaveId).copy(
                    tilordnetRessurs = ""
            )
            lagreOppgave(oppgave, temagruppe, saksbehandlersValgteEnhet)
        } catch (e: Exception) {
            throw RuntimeException("Oppgaven kunne ikke lagres, den er for øyeblikket låst av en annen bruker.", e)
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
        } catch (e: Exception) {
            null
        }
    }

    private fun oppgaveJsonDTOToOppgaveResponse(response: OppgaveJsonDTO): OppgaveResponse {
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

    fun GetOppgaveResponseJsonDTO.toOppgaveJsonDTO() : OppgaveJsonDTO = OppgaveJsonDTO(
            tildeltEnhetsnr = tildeltEnhetsnr,
            oppgavetype = oppgavetype,
            versjon = versjon,
            prioritet = OppgaveJsonDTO.Prioritet.valueOf(GetOppgaveResponseJsonDTO.Prioritet.valueOf(prioritet.value).value),
            status = OppgaveJsonDTO.Status.valueOf(GetOppgaveResponseJsonDTO.Status.valueOf(status.value).value),
            aktivDato = aktivDato,
            id = id,
            endretAvEnhetsnr = endretAvEnhetsnr,
            journalpostId = journalpostId,
            journalpostkilde = journalpostkilde,
            behandlesAvApplikasjon = behandlesAvApplikasjon,
            saksreferanse = saksreferanse,
            bnr = bnr,
            samhandlernr = samhandlernr,
            aktoerId = aktoerId,
            identer = identer,
            orgnr = orgnr,
            tilordnetRessurs = tilordnetRessurs,
            beskrivelse = beskrivelse,
            temagruppe = temagruppe,
            tema = tema,
            behandlingstema = behandlingstema,
            behandlingstype = behandlingstype,
            mappeId = mappeId,
            opprettetAv = opprettetAv,
            endretAv = endretAv,
            metadata = metadata,
            fristFerdigstillelse = fristFerdigstillelse,
            opprettetTidspunkt = opprettetTidspunkt,
            ferdigstiltTidspunkt = ferdigstiltTidspunkt,
            endretTidspunkt = endretTidspunkt
    )

    private fun stripTemakode(prioritet: String): String {
        return prioritet.substringBefore("_")
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
}

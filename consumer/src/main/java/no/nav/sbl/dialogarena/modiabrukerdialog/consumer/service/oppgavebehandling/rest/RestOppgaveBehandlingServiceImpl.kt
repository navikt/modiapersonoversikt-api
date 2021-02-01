package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling.rest

import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.SubjectHandler
import no.nav.common.log.MDCConstants
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.MetadataKey
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe.ANSOS
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe.OKSOS
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.apis.OppgaveApi
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.models.OppgaveJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.models.PostOppgaveRequestJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.toOppgaveJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.toPutOppgaveRequestJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentIdent
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.*
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.ArbeidsfordelingV1Service
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.KodeverksmapperService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.domain.Behandling
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling.rest.Utils.SPORSMAL_OG_SVAR
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling.rest.Utils.beskrivelseInnslag
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling.rest.Utils.endretAvEnhet
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling.rest.Utils.leggTilBeskrivelse
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.pdl.PdlSyntetiskMapper
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.time.Clock
import java.time.LocalDate
import java.util.*
import java.util.Optional.ofNullable

class RestOppgaveBehandlingServiceImpl(
    private val kodeverksmapperService: KodeverksmapperService,
    private val pdlOppslagService: PdlOppslagService,
    private val ansattService: AnsattService,
    private val arbeidsfordelingService: ArbeidsfordelingV1Service,
    private val tilgangskontroll: Tilgangskontroll,
    private val stsService: SystemUserTokenProvider,
    private val apiClient: OppgaveApi = OppgaveApiFactory.createClient {
        SubjectHandler.getSsoToken(SsoToken.Type.OIDC).orElseThrow { IllegalStateException("Fant ikke OIDC-token") }
    },
    private val systemApiClient: OppgaveApi = OppgaveApiFactory.createClient {
        stsService.systemUserToken
    },
    private val clock: Clock = Clock.systemDefaultZone()
) : OppgaveBehandlingService {
    private val plukkOppgaveApi = PlukkOppgaveApi(
        apiClient,
        kodeverksmapperService
    )

    override fun opprettOppgave(request: OpprettOppgaveRequest?): OpprettOppgaveResponse {
        requireNotNull(request)
        val ident: String = SubjectHandler.getIdent().orElseThrow { IllegalStateException("Fant ikke ident") }
        val behandling = kodeverksmapperService.mapUnderkategori(request.underkategoriKode)
        val oppgaveType = kodeverksmapperService.mapOppgavetype(request.oppgavetype)
        val aktorId = getAktorId(request.fnr) ?: throw IllegalArgumentException("Fant ikke aktorId for ${request.fnr}")

        val response = apiClient.opprettOppgave(
            xminusCorrelationMinusID = correlationId(),
            postOppgaveRequestJsonDTO = PostOppgaveRequestJsonDTO(
                opprettetAvEnhetsnr = request.opprettetavenhetsnummer.coerceBlankToNull(),
                aktoerId = aktorId,
                behandlesAvApplikasjon = request.behandlesAvApplikasjon.coerceBlankToNull(),
                tildeltEnhetsnr = request.ansvarligEnhetId.coerceBlankToNull(),
                tilordnetRessurs = request.ansvarligIdent.coerceBlankToNull(),
                beskrivelse =  beskrivelseInnslag(
                    ident = ident,
                    navn = ansattService.hentAnsattNavn(ident),
                    enhet = request.opprettetavenhetsnummer,
                    innhold = request.beskrivelse,
                    clock = clock
                ),
                tema = request.tema.coerceBlankToNull(),
                oppgavetype = oppgaveType,
                behandlingstema = behandling.map(Behandling::getBehandlingstema).orElse(null),
                behandlingstype = behandling.map(Behandling::getBehandlingstype).orElse(null),
                aktivDato = LocalDate.now(clock),
                fristFerdigstillelse = request.oppgaveFrist,
                prioritet = PostOppgaveRequestJsonDTO.Prioritet.valueOf(stripTemakode(request.prioritet)),
                metadata = request.behandlingskjedeId.coerceBlankToNull()
                    ?.let { mapOf(MetadataKey.EKSTERN_HENVENDELSE_ID.name to it) }
            )
        )

        val oppgaveId = response.id ?: throw IllegalStateException("Response inneholdt ikke oppgaveId")
        return OpprettOppgaveResponse(oppgaveId.toString())
    }

    override fun opprettSkjermetOppgave(request: OpprettSkjermetOppgaveRequest?): OpprettOppgaveResponse {
        requireNotNull(request)
        val ident: String = SubjectHandler.getIdent().orElseThrow { IllegalStateException("Fant ikke ident") }
        val behandling = kodeverksmapperService.mapUnderkategori(request.underkategoriKode)
        val oppgaveType = kodeverksmapperService.mapOppgavetype(request.oppgavetype)
        val aktorId = getAktorId(request.fnr) ?: throw IllegalArgumentException("Fant ikke aktorId for ${request.fnr}")

        val response = apiClient.opprettOppgave(
            xminusCorrelationMinusID = correlationId(),
            postOppgaveRequestJsonDTO = PostOppgaveRequestJsonDTO(
                opprettetAvEnhetsnr = request.opprettetavenhetsnummer.coerceBlankToNull(),
                aktoerId = aktorId,
                behandlesAvApplikasjon = request.behandlesAvApplikasjon.coerceBlankToNull(),
                beskrivelse =  beskrivelseInnslag(
                    ident = ident,
                    navn = ansattService.hentAnsattNavn(ident),
                    enhet = request.opprettetavenhetsnummer,
                    innhold = request.beskrivelse,
                    clock = clock
                ),
                tema = request.tema.coerceBlankToNull(),
                oppgavetype = oppgaveType,
                behandlingstema = behandling.map(Behandling::getBehandlingstema).orElse(null),
                behandlingstype = behandling.map(Behandling::getBehandlingstype).orElse(null),
                aktivDato = LocalDate.now(clock),
                fristFerdigstillelse = request.oppgaveFrist,
                prioritet = PostOppgaveRequestJsonDTO.Prioritet.valueOf(stripTemakode(request.prioritet))
            )
        )

        val oppgaveId = response.id ?: throw IllegalStateException("Response inneholdt ikke oppgaveId")
        return OpprettOppgaveResponse(oppgaveId.toString())
    }

    override fun hentOppgave(oppgaveId: String?): Oppgave {
        requireNotNull(oppgaveId)
        val oppgave = hentOppgaveJsonDTO(oppgaveId)

        return mapTilOppgave(oppgave)
    }

    override fun tilordneOppgaveIGsak(oppgaveId: String?, temagruppe: Temagruppe?, saksbehandlersValgteEnhet: String?) {
        requireNotNull(oppgaveId)
        val ident: String = SubjectHandler.getIdent().orElseThrow { IllegalStateException("Fant ikke ident") }

        val oppgave = hentOppgaveJsonDTO(oppgaveId)

        apiClient.endreOppgave(
            correlationId(),
            oppgaveId.toLong(),
            oppgave.copy(
                tilordnetRessurs = ident,
                endretAvEnhetsnr = endretAvEnhet(temagruppe, saksbehandlersValgteEnhet)
            ).toPutOppgaveRequestJsonDTO()
        )
    }

    override fun finnTildelteOppgaverIGsak(): MutableList<Oppgave> {
        val ident: String = SubjectHandler.getIdent().orElseThrow { IllegalStateException("Fant ikke ident") }
        val response = apiClient.finnOppgaver(
            correlationId(),
            tilordnetRessurs = ident,
            aktivDatoTom = LocalDate.now(clock).toString(),
            statuskategori = "AAPEN"
        )

        val oppgaver = response.oppgaver ?: emptyList()
        return filtrerUtOppgaverMedManglendeTilgang(oppgaver)
            .map(this::mapTilOppgave)
            .toMutableList()
    }

    override fun plukkOppgaverFraGsak(
        temagruppe: Temagruppe?,
        saksbehandlersValgteEnhet: String?
    ): MutableList<Oppgave> {
        return plukkOppgaveApi.plukkOppgaverFraGsak(temagruppe, saksbehandlersValgteEnhet)
            .map(this::mapTilOppgave)
            .toMutableList()
    }

    override fun ferdigstillOppgaveIGsak(
        oppgaveId: String?,
        temagruppe: Temagruppe?,
        saksbehandlersValgteEnhet: String?
    ) {
        ferdigstillOppgaveIGsak(oppgaveId, ofNullable(temagruppe), saksbehandlersValgteEnhet)
    }

    override fun ferdigstillOppgaveIGsak(
        oppgaveId: String?,
        temagruppe: Optional<Temagruppe>?,
        saksbehandlersValgteEnhet: String?
    ) {
        ferdigstillOppgaveIGsak(oppgaveId, temagruppe, saksbehandlersValgteEnhet, "")
    }

    override fun ferdigstillOppgaveIGsak(
        oppgaveId: String?,
        temagruppe: Optional<Temagruppe>?,
        saksbehandlersValgteEnhet: String?,
        beskrivelse: String?
    ) {
        requireNotNull(oppgaveId)
        val ident: String = SubjectHandler.getIdent().orElseThrow { IllegalStateException("Fant ikke ident") }
        val oppgave = hentOppgaveJsonDTO(oppgaveId)

        apiClient.endreOppgave(
            correlationId(),
            oppgaveId.toLong(),
            oppgave.copy(
                status = OppgaveJsonDTO.Status.FERDIGSTILT,
                beskrivelse = leggTilBeskrivelse(
                    oppgave.beskrivelse,
                    beskrivelseInnslag(
                        ident = ident,
                        navn = ansattService.hentAnsattNavn(ident),
                        enhet = saksbehandlersValgteEnhet,
                        innhold = "Oppgaven er ferdigstilt i Modia. $beskrivelse",
                        clock = clock
                    )
                ),
                endretAvEnhetsnr = endretAvEnhet(temagruppe?.orElse(null), saksbehandlersValgteEnhet)
            ).toPutOppgaveRequestJsonDTO()
        )
    }

    override fun ferdigstillOppgaverIGsak(
        oppgaveIder: MutableList<String?>?,
        temagruppe: Optional<Temagruppe>?,
        saksbehandlersValgteEnhet: String?
    ) {
        requireNotNull(oppgaveIder)
        for (oppgaveId in oppgaveIder) {
            ferdigstillOppgaveIGsak(oppgaveId, temagruppe, saksbehandlersValgteEnhet, "")
        }
    }

    override fun leggTilbakeOppgaveIGsak(request: LeggTilbakeOppgaveIGsakRequest?) {
        requireNotNull(request)
        requireNotNull(request.oppgaveId)
        val ident: String = SubjectHandler.getIdent().orElseThrow { IllegalStateException("Fant ikke ident") }
        val oppgave = hentOppgaveJsonDTO(request.oppgaveId)

        if (oppgave.tilordnetRessurs !== ident) {
            val feilmelding = "Innlogget saksbehandler $ident er ikke tilordnet oppgave ${request.oppgaveId}, den er tilordnet: ${oppgave.tilordnetRessurs}"
            throw ResponseStatusException(HttpStatus.FORBIDDEN, feilmelding)
        }

        var oppdatertOppgave = oppgave.copy(
            tilordnetRessurs = null,
            beskrivelse = leggTilBeskrivelse(
                oppgave.beskrivelse,
                beskrivelseInnslag(
                    ident = ident,
                    navn = ansattService.hentAnsattNavn(ident),
                    enhet = request.saksbehandlersValgteEnhet,
                    innhold = request.beskrivelse,
                    clock = clock
                )
            ),
            endretAvEnhetsnr = endretAvEnhet(request.nyTemagruppe, request.saksbehandlersValgteEnhet)
        )
        if (request.nyTemagruppe != null) {
            val behandling = kodeverksmapperService.mapUnderkategori(request.nyTemagruppe.underkategori)
            oppdatertOppgave = oppdatertOppgave.copy(
                tildeltEnhetsnr = finnAnsvarligEnhet(oppgave, request.nyTemagruppe),
                behandlingstema = behandling.map(Behandling::getBehandlingstema).orElse(null),
                behandlingstype = behandling.map(Behandling::getBehandlingstype).orElse(null)
            )
        }

        apiClient.endreOppgave(
            correlationId(),
            request.oppgaveId.toLong(),
            oppdatertOppgave.toPutOppgaveRequestJsonDTO()
        )
    }

    override fun systemLeggTilbakeOppgaveIGsak(
        oppgaveId: String?,
        temagruppe: Temagruppe?,
        saksbehandlersValgteEnhet: String?
    ) {
        requireNotNull(oppgaveId)
        val oppgave = hentOppgaveJsonDTO(oppgaveId)
        systemApiClient
            .endreOppgave(
                correlationId(),
                oppgaveId.toLong(),
                oppgave.copy(
                    tilordnetRessurs = null,
                    endretAvEnhetsnr = endretAvEnhet(temagruppe, saksbehandlersValgteEnhet)
                ).toPutOppgaveRequestJsonDTO()
            )
    }

    override fun oppgaveErFerdigstilt(oppgaveId: String?): Boolean {
        requireNotNull(oppgaveId)
        val oppgave = hentOppgaveJsonDTO(oppgaveId)
        return oppgave.status == OppgaveJsonDTO.Status.FERDIGSTILT
    }

    private fun hentOppgaveJsonDTO(oppgaveId: String): OppgaveJsonDTO {
        return apiClient.hentOppgave(
            xminusCorrelationMinusID = correlationId(),
            id = oppgaveId.toLong()
        ).toOppgaveJsonDTO()
    }

    private fun finnAnsvarligEnhet(oppgave: OppgaveJsonDTO, temagruppe: Temagruppe): String {
        val aktorId = requireNotNull(oppgave.aktoerId)
        val enheter: List<AnsattEnhet> = arbeidsfordelingService.finnBehandlendeEnhetListe(
            getFnr(aktorId),
            oppgave.tema,
            oppgave.oppgavetype,
            underkategoriOverstyringForArbeidsfordeling(temagruppe)
        )
        return enheter.firstOrNull()?.enhetId ?: oppgave.tildeltEnhetsnr
    }

    private fun underkategoriOverstyringForArbeidsfordeling(temagruppe: Temagruppe): String {
        val overstyrtTemagruppe = if (temagruppe == OKSOS) ANSOS else temagruppe
        return overstyrtTemagruppe.underkategori
    }

    private fun mapTilOppgave(oppgave: OppgaveJsonDTO): Oppgave {
        val oppgaveId = requireNotNull(oppgave.id) {
            "OppgaveId må være satt for konvertering til Oppgave"
        }
        val aktorId = requireNotNull(oppgave.aktoerId)
        val fnr = requireNotNull(getFnr(aktorId)) {
            "Fant ikke fnr for aktorId $aktorId"
        }
        val henvendelseId = oppgave.metadata?.get(MetadataKey.EKSTERN_HENVENDELSE_ID.name)
        val erSTO = oppgave.oppgavetype
            .let { it == SPORSMAL_OG_SVAR }

        return Oppgave(
            oppgaveId.toString(),
            fnr,
            henvendelseId,
            erSTO
        )
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

    private fun getFnr(aktorId: String): String? {
        return try {
            pdlOppslagService
                .hentIdent(aktorId)
                ?.identer
                ?.find { ident -> ident.gruppe == HentIdent.IdentGruppe.FOLKEREGISTERIDENT }
                ?.ident
                ?.let(PdlSyntetiskMapper::mapFnrTilPdl)
        } catch (e: Exception) {
            null
        }
    }

    private fun filtrerUtOppgaverMedManglendeTilgang(oppgaver: List<OppgaveJsonDTO>): List<OppgaveJsonDTO> {
        val aktoerer = oppgaver.groupBy { it.aktoerId }
        val tilganger = aktoerer
            .map { entry ->
                val aktoerId = entry.key
                val isPermit =
                    if (aktoerId.isNullOrEmpty()) {
                        false
                    } else {
                        tilgangskontroll
                            .check(Policies.tilgangTilBrukerMedAktorId.with(aktoerId))
                            .getDecision()
                            .isPermit()
                    }

                Triple(entry.key, entry.value, isPermit)
            }

        val oppgaverSomSkalLeggesTilbake: List<OppgaveJsonDTO> = tilganger
            .filter { (_, _, isPermit) -> !isPermit }
            .flatMap { (_, oppgavelist, _) -> oppgavelist }
        val oppgaverSomErGodkjent = oppgaver.minus(oppgaverSomSkalLeggesTilbake)

        for (oppgave in oppgaverSomSkalLeggesTilbake) {
            systemApiClient.endreOppgave(
                correlationId(),
                requireNotNull(oppgave.id) { "Kan ikke legge tilbake oppgave uten oppgaveId" },
                oppgave
                    .copy(
                        tilordnetRessurs = null,
                        endretAvEnhetsnr = endretAvEnhet(null, "4100")
                    )
                    .toPutOppgaveRequestJsonDTO()
            )
        }

        return oppgaverSomErGodkjent
    }

    private fun correlationId() = MDC.get(MDCConstants.MDC_CALL_ID) ?: UUID.randomUUID().toString()
    private fun stripTemakode(prioritet: String) = prioritet.substringBefore("_")
    private fun String?.coerceBlankToNull() = if (this == null || this.isBlank()) null else this
}

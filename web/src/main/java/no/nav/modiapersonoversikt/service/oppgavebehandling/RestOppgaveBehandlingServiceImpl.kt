package no.nav.modiapersonoversikt.service.oppgavebehandling

import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.http.getCallId
import no.nav.modiapersonoversikt.infrastructure.rsbac.DecisionEnums
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.legacy.api.domain.MetadataKey
import no.nav.modiapersonoversikt.legacy.api.domain.Oppgave
import no.nav.modiapersonoversikt.legacy.api.domain.Temagruppe
import no.nav.modiapersonoversikt.legacy.api.domain.Temagruppe.ANSOS
import no.nav.modiapersonoversikt.legacy.api.domain.Temagruppe.OKSOS
import no.nav.modiapersonoversikt.legacy.api.domain.oppgave.generated.apis.OppgaveApi
import no.nav.modiapersonoversikt.legacy.api.domain.oppgave.generated.models.GetOppgaverResponseJsonDTO
import no.nav.modiapersonoversikt.legacy.api.domain.oppgave.generated.models.OppgaveJsonDTO
import no.nav.modiapersonoversikt.legacy.api.domain.oppgave.generated.models.PostOppgaveRequestJsonDTO
import no.nav.modiapersonoversikt.legacy.api.domain.oppgave.toOppgaveJsonDTO
import no.nav.modiapersonoversikt.legacy.api.domain.oppgave.toPutOppgaveRequestJsonDTO
import no.nav.modiapersonoversikt.legacy.api.service.*
import no.nav.modiapersonoversikt.legacy.api.service.OppgaveBehandlingService.AlleredeTildeltAnnenSaksbehandler
import no.nav.modiapersonoversikt.legacy.api.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.modiapersonoversikt.service.arbeidsfordeling.ArbeidsfordelingService
import no.nav.modiapersonoversikt.service.kodeverksmapper.KodeverksmapperService
import no.nav.modiapersonoversikt.service.kodeverksmapper.domain.Behandling
import no.nav.modiapersonoversikt.service.kodeverksmapper.domain.parseV2BehandlingString
import no.nav.modiapersonoversikt.service.oppgavebehandling.Utils.OPPGAVE_MAX_LIMIT
import no.nav.modiapersonoversikt.service.oppgavebehandling.Utils.SPORSMAL_OG_SVAR
import no.nav.modiapersonoversikt.service.oppgavebehandling.Utils.beskrivelseInnslag
import no.nav.modiapersonoversikt.service.oppgavebehandling.Utils.defaultEnhetGittTemagruppe
import no.nav.modiapersonoversikt.service.oppgavebehandling.Utils.leggTilBeskrivelse
import no.nav.modiapersonoversikt.service.oppgavebehandling.Utils.paginering
import no.nav.modiapersonoversikt.utils.SafeListAggregate
import org.slf4j.LoggerFactory
import java.time.Clock
import java.time.LocalDate
import java.util.*
import java.util.Optional.ofNullable

private val tjenestekallLogg = LoggerFactory.getLogger("SecureLog")

class RestOppgaveBehandlingServiceImpl(
    private val kodeverksmapperService: KodeverksmapperService,
    private val pdlOppslagService: PdlOppslagService,
    private val ansattService: AnsattService,
    private val arbeidsfordelingService: ArbeidsfordelingService,
    private val tilgangskontroll: Tilgangskontroll,
    private val stsService: SystemUserTokenProvider,
    private val apiClient: OppgaveApi = OppgaveApiFactory.createClient {
        AuthContextUtils.requireToken()
    },
    private val systemApiClient: OppgaveApi = OppgaveApiFactory.createClient {
        stsService.systemUserToken
    },
    private val clock: Clock = Clock.systemDefaultZone()
) : OppgaveBehandlingService {

    companion object {
        @JvmStatic
        fun create(
            kodeverksmapperService: KodeverksmapperService,
            pdlOppslagService: PdlOppslagService,
            ansattService: AnsattService,
            arbeidsfordelingService: ArbeidsfordelingService,
            tilgangskontroll: Tilgangskontroll,
            stsService: SystemUserTokenProvider
        ): OppgaveBehandlingService = RestOppgaveBehandlingServiceImpl(
            kodeverksmapperService = kodeverksmapperService,
            pdlOppslagService = pdlOppslagService,
            ansattService = ansattService,
            arbeidsfordelingService = arbeidsfordelingService,
            tilgangskontroll = tilgangskontroll,
            stsService = stsService
        )
    }

    override fun opprettOppgave(request: OpprettOppgaveRequest?): OpprettOppgaveResponse {
        requireNotNull(request)
        val ident: String = AuthContextUtils.requireIdent()
        val behandling: Behandling? = request.underkategoriKode?.parseV2BehandlingString()
        val aktorId = pdlOppslagService.hentAktorId(request.fnr)
            ?: throw IllegalArgumentException("Fant ikke aktorId for ${request.fnr}")

        val response = apiClient.opprettOppgave(
            xCorrelationID = correlationId(),
            postOppgaveRequestJsonDTO = PostOppgaveRequestJsonDTO(
                opprettetAvEnhetsnr = request.opprettetavenhetsnummer.coerceBlankToNull(),
                aktoerId = aktorId,
                behandlesAvApplikasjon = request.behandlesAvApplikasjon.coerceBlankToNull(),
                tildeltEnhetsnr = request.ansvarligEnhetId.coerceBlankToNull(),
                tilordnetRessurs = request.ansvarligIdent.coerceBlankToNull(),
                beskrivelse = beskrivelseInnslag(
                    ident = ident,
                    navn = ansattService.hentAnsattNavn(ident),
                    enhet = request.opprettetavenhetsnummer,
                    innhold = request.beskrivelse,
                    clock = clock
                ),
                tema = request.tema,
                oppgavetype = request.oppgavetype,
                behandlingstema = behandling?.behandlingstema,
                behandlingstype = behandling?.behandlingstype,
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
        val ident: String = AuthContextUtils.requireIdent()
        val behandling: Behandling? = request.underkategoriKode?.parseV2BehandlingString()
        val aktorId = pdlOppslagService.hentAktorId(request.fnr)
            ?: throw IllegalArgumentException("Fant ikke aktorId for ${request.fnr}")

        val response = systemApiClient.opprettOppgave(
            xCorrelationID = correlationId(),
            postOppgaveRequestJsonDTO = PostOppgaveRequestJsonDTO(
                opprettetAvEnhetsnr = request.opprettetavenhetsnummer.coerceBlankToNull(),
                aktoerId = aktorId,
                behandlesAvApplikasjon = request.behandlesAvApplikasjon.coerceBlankToNull(),
                beskrivelse = beskrivelseInnslag(
                    ident = ident,
                    navn = ansattService.hentAnsattNavn(ident),
                    enhet = request.opprettetavenhetsnummer,
                    innhold = request.beskrivelse,
                    clock = clock
                ),
                tema = request.tema,
                oppgavetype = request.oppgavetype,
                behandlingstema = behandling?.behandlingstema,
                behandlingstype = behandling?.behandlingstype,
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

    override fun tilordneOppgaveIGsak(
        oppgaveId: String?,
        temagruppe: Temagruppe?,
        saksbehandlersValgteEnhet: String?,
        tvungenTilordning: Boolean
    ) {
        requireNotNull(oppgaveId)
        val ident: String = AuthContextUtils.requireIdent()

        val oppgave = hentOppgaveJsonDTO(oppgaveId)
        if (oppgave.tilordnetRessurs == ident) {
            return
        } else if (tvungenTilordning) {
            tjenestekallLogg.warn("[OPPGAVE] $ident gjorde en tvungen tilordning av $oppgaveId, som allerede var tildelt ${oppgave.tilordnetRessurs}")
        } else if (oppgave.tilordnetRessurs != null && oppgave.tilordnetRessurs != ident) {
            throw AlleredeTildeltAnnenSaksbehandler("Oppgaven er allerede tildelt ${oppgave.tilordnetRessurs}. Vil du overstyre dette?")
        }

        apiClient.endreOppgave(
            correlationId(),
            oppgaveId.toLong(),
            oppgave.copy(
                tilordnetRessurs = ident,
                endretAvEnhetsnr = defaultEnhetGittTemagruppe(temagruppe, saksbehandlersValgteEnhet)
            ).toPutOppgaveRequestJsonDTO()
        )
    }

    override fun finnTildelteOppgaverIGsak(): MutableList<Oppgave> {
        val ident: String = AuthContextUtils.requireIdent()
        val correlationId = correlationId()

        return hentOppgaverPaginertOgTilgangskontroll { offset ->
            apiClient.finnOppgaver(
                correlationId,
                tilordnetRessurs = ident,
                aktivDatoTom = LocalDate.now(clock).toString(),
                statuskategori = "AAPEN",
                limit = OPPGAVE_MAX_LIMIT,
                offset = offset
            )
        }
    }

    override fun finnTildelteOppgaverIGsak(fnr: String): MutableList<Oppgave> {
        val ident: String = AuthContextUtils.requireIdent()
        val aktorId = pdlOppslagService.hentAktorId(fnr)
            ?: throw IllegalArgumentException("Fant ikke aktorId for $fnr")
        val correlationId = correlationId()

        return hentOppgaverPaginertOgTilgangskontroll { offset ->
            apiClient.finnOppgaver(
                correlationId,
                aktoerId = listOf(aktorId),
                tilordnetRessurs = ident,
                aktivDatoTom = LocalDate.now(clock).toString(),
                statuskategori = "AAPEN",
                limit = OPPGAVE_MAX_LIMIT,
                offset = offset
            )
        }
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
        val ident: String = AuthContextUtils.requireIdent()
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
                endretAvEnhetsnr = defaultEnhetGittTemagruppe(temagruppe?.orElse(null), saksbehandlersValgteEnhet)
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

    override fun systemLeggTilbakeOppgaveIGsak(
        oppgaveId: String?,
        temagruppe: Temagruppe?,
        saksbehandlersValgteEnhet: String?
    ) {
        requireNotNull(oppgaveId)
        /**
         * NB Viktig at systemApiClient brukes her.
         * Vi skal potensielt sett hente en oppgave saksbehandler ikke har tilgang til.
         */
        val oppgave = systemApiClient.hentOppgave(
            xCorrelationID = correlationId(),
            id = oppgaveId.toLong()
        ).toOppgaveJsonDTO()

        tjenestekallLogg.warn("[OPPGAVE] systemLeggTilbakeOppgaveIGsak la tilbake oppgaver pga manglende tilgang: $oppgaveId")
        systemApiClient
            .endreOppgave(
                correlationId(),
                oppgaveId.toLong(),
                oppgave.copy(
                    tilordnetRessurs = null,
                    endretAvEnhetsnr = defaultEnhetGittTemagruppe(temagruppe, saksbehandlersValgteEnhet)
                ).toPutOppgaveRequestJsonDTO()
            )
    }

    override fun oppgaveErFerdigstilt(oppgaveId: String?): Boolean {
        requireNotNull(oppgaveId)
        val oppgave = hentOppgaveJsonDTO(oppgaveId)
        return oppgave.status == OppgaveJsonDTO.Status.FERDIGSTILT
    }

    override fun finnOgTilordneSTOOppgave(
        fnr: String,
        henvendelseId: String,
        temagruppe: Temagruppe?,
        enhet: String?,
        tvungenTilordning: Boolean
    ): Oppgave? {
        val aktorId = pdlOppslagService.hentAktorId(fnr)
            ?: throw IllegalArgumentException("Fant ikke aktorId for $fnr")

        val correlationId = correlationId()

        val oppgaver = hentOppgaverPaginertOgTilgangskontroll { offset ->
            apiClient.finnOppgaver(
                aktoerId = listOf(aktorId),
                xCorrelationID = correlationId,
                tema = listOf(Utils.KONTAKT_NAV),
                oppgavetype = listOf(SPORSMAL_OG_SVAR),
                aktivDatoTom = LocalDate.now(clock).toString(),
                statuskategori = "AAPEN",
                limit = OPPGAVE_MAX_LIMIT,
                offset = offset
            )
        }
        val henvendelseOppgave = oppgaver.firstOrNull { it.henvendelseId == henvendelseId }
        if (henvendelseOppgave != null) {
            tilordneOppgaveIGsak(
                henvendelseOppgave.oppgaveId,
                temagruppe,
                enhet,
                tvungenTilordning
            )
        }

        return henvendelseOppgave
    }

    private fun hentOppgaveJsonDTO(oppgaveId: String): OppgaveJsonDTO {
        return apiClient.hentOppgave(
            xCorrelationID = correlationId(),
            id = oppgaveId.toLong()
        ).toOppgaveJsonDTO()
    }

    private fun hentOppgaverPaginertOgTilgangskontroll(action: (offset: Long) -> GetOppgaverResponseJsonDTO): MutableList<Oppgave> {
        val response = paginering(
            total = { it.antallTreffTotalt ?: 0 },
            data = { it.oppgaver ?: emptyList() },
            action = action
        )

        val oppgaver = response
            .filter { oppgaveJson ->
                val erTilknyttetHenvendelse =
                    oppgaveJson.metadata?.containsKey(MetadataKey.EKSTERN_HENVENDELSE_ID.name) ?: false
                val harAktorId = !oppgaveJson.aktoerId.isNullOrBlank()
                erTilknyttetHenvendelse && harAktorId
            }

        val aktorIdTilganger: Map<String?, DecisionEnums> = hentAktorIdTilgang(oppgaver)
        return SafeListAggregate<OppgaveJsonDTO, OppgaveJsonDTO>(oppgaver)
            .filter { aktorIdTilganger[it.aktoerId] == DecisionEnums.PERMIT }
            .fold(
                transformSuccess = this::mapTilOppgave,
                transformFailure = { it }
            )
            .getWithFailureHandling { failures ->
                val oppgaveIds = failures.joinToString(", ") { it.id?.toString() ?: "Mangler oppgave id" }
                tjenestekallLogg.warn("[OPPGAVE] hentOppgaverPaginertOgTilgangskontroll la tilbake oppgaver pga manglende tilgang: $oppgaveIds")
                systemLeggTilbakeOppgaver(failures)
            }
            .toMutableList()
    }

    private fun finnAnsvarligEnhet(oppgave: OppgaveJsonDTO, temagruppe: Temagruppe): String {
        val aktorId = requireNotNull(oppgave.aktoerId)
        val enheter: List<NorgDomain.Enhet> = arbeidsfordelingService.hentBehandlendeEnheter(
            brukerIdent = Fnr.of(pdlOppslagService.hentFnr(aktorId)),
            fagomrade = oppgave.tema,
            oppgavetype = oppgave.oppgavetype,
            underkategori = underkategoriOverstyringForArbeidsfordeling(temagruppe)
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
        val fnr = requireNotNull(pdlOppslagService.hentFnr(aktorId)) {
            "Fant ikke fnr for aktorId $aktorId"
        }
        val henvendelseId = oppgave.metadata?.get(MetadataKey.EKSTERN_HENVENDELSE_ID.name)
        val erSTO = oppgave.oppgavetype == SPORSMAL_OG_SVAR

        return Oppgave(
            oppgaveId.toString(),
            fnr,
            henvendelseId,
            erSTO
        )
    }

    private fun hentAktorIdTilgang(oppgaver: List<OppgaveJsonDTO>): Map<String?, DecisionEnums> {
        val aktoerer = oppgaver.groupBy { it.aktoerId }
        return aktoerer
            .map { entry ->
                val aktoerId = entry.key
                val decision =
                    if (aktoerId.isNullOrEmpty()) {
                        DecisionEnums.DENY
                    } else {
                        tilgangskontroll
                            .check(Policies.tilgangTilBrukerMedAktorId.with(aktoerId))
                            .getDecision()
                            .value
                    }
                Pair(aktoerId, decision)
            }
            .toMap()
    }

    private fun systemLeggTilbakeOppgaver(oppgaver: List<OppgaveJsonDTO>) {
        for (oppgave in oppgaver) {
            systemApiClient.endreOppgave(
                correlationId(),
                requireNotNull(oppgave.id) { "Kan ikke legge tilbake oppgave uten oppgaveId" },
                oppgave
                    .copy(
                        tilordnetRessurs = null,
                        endretAvEnhetsnr = defaultEnhetGittTemagruppe(null, "4100")
                    )
                    .toPutOppgaveRequestJsonDTO()
            )
        }
    }

    private fun correlationId() = getCallId()
    private fun stripTemakode(prioritet: String) = prioritet.substringBefore("_")
    private fun String?.coerceBlankToNull() = if (this == null || this.isBlank()) null else this
}

package no.nav.modiapersonoversikt.rest.dialog.salesforce

import no.nav.modiapersonoversikt.commondomain.Temagruppe
import no.nav.modiapersonoversikt.consumer.ldap.LDAPService
import no.nav.modiapersonoversikt.consumer.ldap.Saksbehandler
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.models.*
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.models.MeldingDTO.*
import no.nav.modiapersonoversikt.rest.DATO_TID_FORMAT
import no.nav.modiapersonoversikt.rest.RestUtils
import no.nav.modiapersonoversikt.rest.dialog.apis.*
import no.nav.modiapersonoversikt.rest.dialog.apis.MeldingDTO
import no.nav.modiapersonoversikt.rest.dialog.domain.Meldingstype
import no.nav.modiapersonoversikt.rest.dialog.domain.Status
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.enhetligkodeverk.KodeverkConfig
import no.nav.modiapersonoversikt.service.oppgavebehandling.Oppgave
import no.nav.modiapersonoversikt.service.oppgavebehandling.OppgaveBehandlingService
import no.nav.modiapersonoversikt.service.sfhenvendelse.EksternBruker
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.NotSupportedException

private val REFERAT_TYPER = listOf(
    Meldingstype.SAMTALEREFERAT_TELEFON,
    Meldingstype.SAMTALEREFERAT_OPPMOTE
)

class SfLegacyDialogController(
    private val sfHenvendelseService: SfHenvendelseService,
    private val oppgaveBehandlingService: OppgaveBehandlingService,
    private val ldapService: LDAPService,
    private val kodeverk: EnhetligKodeverk.Service,
    private val unleash: UnleashService
) : DialogApi {
    private val logger = LoggerFactory.getLogger(SfLegacyDialogController::class.java)
    override fun hentMeldinger(request: HttpServletRequest, fnr: String, enhet: String?): List<TraadDTO> {
        val valgtEnhet = RestUtils.hentValgtEnhet(enhet, request)
        val bruker = EksternBruker.Fnr(fnr)

        val sfHenvendelser = sfHenvendelseService.hentHenvendelser(bruker, valgtEnhet)
        val dialogMappingContext = lagMappingContext(sfHenvendelser)
        return sfHenvendelser
            .map {
                dialogMappingContext.mapSfHenvendelserTilLegacyFormat(it)
            }
    }

    override fun sendMelding(request: HttpServletRequest, fnr: String, referatRequest: SendReferatRequest): TraadDTO {
        val henvendelse = sfHenvendelseService.sendSamtalereferat(
            kjedeId = null,
            bruker = EksternBruker.Fnr(fnr),
            enhet = RestUtils.hentValgtEnhet(null, request),
            temagruppe = referatRequest.temagruppe,
            kanal = referatRequest.meldingstype.getKanal(),
            fritekst = referatRequest.fritekst
        )

        return parseFraHenvendelseTilTraad(henvendelse)
    }

    override fun sendSporsmal(
        request: HttpServletRequest,
        fnr: String,
        sporsmalsRequest: SendSporsmalRequest
    ): TraadDTO {
        val enhet = RestUtils.hentValgtEnhet(sporsmalsRequest.enhet, request)
        val henvendelse = sfHenvendelseService.opprettNyDialogOgSendMelding(
            bruker = EksternBruker.Fnr(fnr),
            enhet = enhet,
            temagruppe = SfTemagruppeTemaMapping.hentTemagruppeForTema(sporsmalsRequest.sak.temaKode),
            tilknyttetAnsatt = sporsmalsRequest.erOppgaveTilknyttetAnsatt,
            fritekst = sporsmalsRequest.fritekst
        )
        sfHenvendelseService.journalforHenvendelse(
            enhet = enhet,
            kjedeId = henvendelse.kjedeId,
            saksId = sporsmalsRequest.sak.fagsystemSaksId,
            saksTema = sporsmalsRequest.sak.temaKode,
            fagsakSystem = sporsmalsRequest.sak.fagsystemKode
        )

        return parseFraHenvendelseTilTraad(henvendelse)
    }

    override fun sendInfomelding(
        request: HttpServletRequest,
        fnr: String,
        infomeldingRequest: InfomeldingRequest
    ): TraadDTO {
        val enhet = RestUtils.hentValgtEnhet(infomeldingRequest.enhet, request)
        val henvendelse = sfHenvendelseService.opprettNyDialogOgSendMelding(
            bruker = EksternBruker.Fnr(fnr),
            enhet = enhet,
            temagruppe = SfTemagruppeTemaMapping.hentTemagruppeForTema(infomeldingRequest.sak.temaKode),
            tilknyttetAnsatt = false,
            fritekst = infomeldingRequest.fritekst
        )

        sfHenvendelseService.lukkTraad(henvendelse.kjedeId)
        sfHenvendelseService.journalforHenvendelse(
            enhet = enhet,
            kjedeId = henvendelse.kjedeId,
            saksId = infomeldingRequest.sak.fagsystemSaksId,
            saksTema = infomeldingRequest.sak.temaKode,
            fagsakSystem = infomeldingRequest.sak.fagsystemKode
        )

        return parseFraHenvendelseTilTraad(henvendelse)
    }

    override fun startFortsettDialog(
        request: HttpServletRequest,
        fnr: String,
        ignorerConflict: Boolean?,
        opprettHenvendelseRequest: OpprettHenvendelseRequest
    ): FortsettDialogDTO {
        /**
         * Artifakt av legacy-henvendelse, beholdt for å holde apiene like.
         */
        val traad = sfHenvendelseService.hentHenvendelse(opprettHenvendelseRequest.traadId)
        val oppgaveId: String? =
            finnOgTilordneOppgaveIdTilTrad(traad, fnr, opprettHenvendelseRequest.enhet, ignorerConflict ?: false)

        return FortsettDialogDTO(opprettHenvendelseRequest.traadId, oppgaveId)
    }

    override fun sendFortsettDialog(
        request: HttpServletRequest,
        fnr: String,
        fortsettDialogRequest: FortsettDialogRequest
    ): TraadDTO {
        val kjedeId = fortsettDialogRequest.traadId
        val oppgaveId = fortsettDialogRequest.oppgaveId

        val bruker = EksternBruker.Fnr(fnr)
        val enhet = RestUtils.hentValgtEnhet(fortsettDialogRequest.enhet, request)
        var henvendelse = sfHenvendelseService.hentHenvendelse(fortsettDialogRequest.traadId)

        val henvendelseTilhorerBruker = sfHenvendelseService.sjekkEierskap(bruker, henvendelse)
        if (!henvendelseTilhorerBruker) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Henvendelse $kjedeId tilhørte ikke bruker")
        }

        if (oppgaveId != null) {
            val oppgave: Oppgave? = oppgaveBehandlingService.hentOppgave(oppgaveId)
            if (kjedeId != oppgave?.henvendelseId) {
                throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Feil oppgaveId fra client. Forventet '$kjedeId', men fant '${oppgave?.henvendelseId}'"
                )
            } else if (oppgaveBehandlingService.oppgaveErFerdigstilt(oppgaveId)) {
                throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Feil oppgaveId fra client. Oppgaven er allerede ferdigstilt"
                )
            }
        }
        val erSamtalereferat = REFERAT_TYPER.contains(fortsettDialogRequest.meldingstype)
        if (erSamtalereferat) {
            henvendelse = sfHenvendelseService.sendSamtalereferat(
                kjedeId = kjedeId,
                bruker = bruker,
                enhet = enhet,
                temagruppe = henvendelse.gjeldendeTemagruppe!!, // TODO må fikses av SF-api. Temagruppe kan ikke være null
                kanal = fortsettDialogRequest.meldingstype.getKanal(),
                fritekst = fortsettDialogRequest.fritekst
            )
            val journalposter = (henvendelse.journalposter ?: emptyList())
                .distinctBy { it.fagsakId }
            journalposter.forEach {
                sfHenvendelseService.journalforHenvendelse(
                    enhet = enhet,
                    kjedeId = henvendelse.kjedeId,
                    saksId = it.fagsakId,
                    saksTema = it.journalfortTema,
                    fagsakSystem = it.fagsaksystem?.name
                )
            }
        } else {
            henvendelse = sfHenvendelseService.sendMeldingPaEksisterendeDialog(
                bruker = bruker,
                kjedeId = kjedeId,
                enhet = enhet,
                tilknyttetAnsatt = fortsettDialogRequest.erOppgaveTilknyttetAnsatt,
                fritekst = fortsettDialogRequest.fritekst
            )

            if (fortsettDialogRequest.meldingstype !== Meldingstype.SPORSMAL_MODIA_UTGAAENDE) {
                sfHenvendelseService.lukkTraad(henvendelse.kjedeId)
            }
            if (fortsettDialogRequest.sak != null) {
                sfHenvendelseService.journalforHenvendelse(
                    enhet = enhet,
                    kjedeId = henvendelse.kjedeId,
                    saksId = fortsettDialogRequest.sak.fagsystemSaksId,
                    saksTema = fortsettDialogRequest.sak.temaKode,
                    fagsakSystem = fortsettDialogRequest.sak.fagsystemKode
                )
            }
        }
        if (oppgaveId != null) {
            oppgaveBehandlingService.ferdigstillOppgaveIGsak(
                oppgaveId,
                Temagruppe.valueOf(henvendelse.gjeldendeTemagruppe!!), // TODO må fikses av SF-api. Temagruppe kan ikke være null
                enhet
            )
        }

        return parseFraHenvendelseTilTraad(henvendelse)
    }

    override fun slaaSammenTraader(
        request: HttpServletRequest,
        fnr: String,
        slaaSammenRequest: SlaaSammenRequest
    ): Map<String, Any?> {
        throw NotSupportedException("Operasjonen er ikke støttet av Salesforce")
    }

    private fun parseFraHenvendelseTilTraad(henvendelse: HenvendelseDTO): TraadDTO {
        val dialogMappingContext = lagMappingContext(listOf(henvendelse))
        return dialogMappingContext.mapSfHenvendelserTilLegacyFormat(henvendelse)
    }

    private fun finnOgTilordneOppgaveIdTilTrad(
        traad: HenvendelseDTO,
        fnr: String,
        enhet: String?,
        ignorerConflict: Boolean
    ): String? {
        return if (traad.erSporsmalFraBruker()) {
            try {
                oppgaveBehandlingService.finnOgTilordneSTOOppgave(
                    fnr,
                    traad.kjedeId,
                    traad.gjeldendeTemagruppe?.let { Temagruppe.valueOf(it) },
                    enhet,
                    ignorerConflict ?: false
                )?.oppgaveId
            } catch (e: OppgaveBehandlingService.AlleredeTildeltAnnenSaksbehandler) {
                throw ResponseStatusException(HttpStatus.CONFLICT, e.message)
            }
        } else {
            null
        }
    }

    private fun HenvendelseDTO.erSporsmalFraBruker(): Boolean {
        val nyesteMelding = this.meldinger?.maxByOrNull { it.sendtDato }
        return nyesteMelding?.fra?.identType == MeldingFraDTO.IdentType.AKTORID
    }

    data class DialogMappingContext(
        val temakodeMap: Map<String, String>,
        val identMap: Map<String, Saksbehandler>
    )

    private fun lagMappingContext(henvendelser: List<HenvendelseDTO>): DialogMappingContext {
        val temakodeMap = kodeverk.hentKodeverk(KodeverkConfig.ARKIVTEMA).asMap()
        val identer = henvendelser
            .flatMap { henvendelse ->
                val journalportIdenter: List<String>? =
                    henvendelse.journalposter?.mapNotNull { it.journalforerNavIdent } // TODO SF bør ikke svare med null for ident her. Rapportert feil
                val markeringIdenter: List<String>? = henvendelse.markeringer?.mapNotNull { it.markertAv }
                val meldingFraIdenter: List<String>? = henvendelse.meldinger
                    ?.filter { it.fra.identType == MeldingFraDTO.IdentType.NAVIDENT }
                    ?.mapNotNull { it.fra.ident }
                (journalportIdenter ?: emptyList())
                    .plus(markeringIdenter ?: emptyList())
                    .plus(meldingFraIdenter ?: emptyList())
            }
            .distinct()

        val identMap = identer.associateWith { ident ->
            runCatching {
                ldapService.hentSaksbehandler(ident)
            }.recover {
                logger.error("Fant ikke saksbehandler for $ident", it)
                Saksbehandler("-", "-", ident)
            }.getOrThrow()
        }

        return DialogMappingContext(temakodeMap, identMap)
    }

    private fun DialogMappingContext.mapSfHenvendelserTilLegacyFormat(henvendelse: HenvendelseDTO): TraadDTO {
        val journalposter = henvendelse.journalposter?.map {
            tilJournalpostDTO(it)
        } ?: emptyList()

        val kontorsperre: MarkeringDTO? = henvendelse.markeringer
            ?.find { it.markeringstype == MarkeringDTO.Markeringstype.KONTORSPERRE }
        val kontorsperretEnhet: String? = kontorsperre?.kontorsperreGT ?: kontorsperre?.kontorsperreEnhet
        val kontorsperretAv = identMap[kontorsperre?.markertAv ?: ""]
            ?.let { mapOf("fornavn" to it.fornavn, "etternavn" to it.etternavn, "ident" to it.ident) }

        val markertSomFeilsendt = henvendelse.markeringer
            ?.find { it.markeringstype == MarkeringDTO.Markeringstype.FEILSENDT }
        val markertSomFeilsendtAv = identMap[markertSomFeilsendt?.markertAv ?: ""]
            ?.let { mapOf("fornavn" to it.fornavn, "etternavn" to it.etternavn, "ident" to it.ident) }

        val henvendelseErKassert: Boolean = henvendelse.kasseringsDato?.isBefore(OffsetDateTime.now()) == true
        val meldinger: List<MeldingDTO> = (henvendelse.meldinger ?: emptyList()).map { melding ->
            val skrevetAv = when (melding.fra.identType) {
                MeldingFraDTO.IdentType.NAVIDENT, MeldingFraDTO.IdentType.AKTORID -> identMap[melding.fra.ident]
                    ?.let { "${it.navn} (${it.ident})" }
                    ?: "Ukjent"
                MeldingFraDTO.IdentType.SYSTEM -> "Salesforce system"
            }
            MeldingDTO(
                mapOf(
                    "id" to "${henvendelse.kjedeId}---${(melding.hashCode())}",
                    "meldingstype" to meldingstypeFraSfTyper(henvendelse, melding),
                    "temagruppe" to henvendelse.gjeldendeTemagruppe,
                    "skrevetAvTekst" to skrevetAv,
                    "fritekst" to hentFritekstFraMelding(henvendelseErKassert, melding),
                    "lestDato" to melding.lestDato?.format(DateTimeFormatter.ofPattern(DATO_TID_FORMAT)),
                    "status" to when {
                        melding.fra.identType == MeldingFraDTO.IdentType.AKTORID -> Status.IKKE_BESVART
                        melding.lestDato != null -> Status.LEST_AV_BRUKER
                        else -> Status.IKKE_LEST_AV_BRUKER
                    },
                    "opprettetDato" to melding.sendtDato.format(DateTimeFormatter.ofPattern(DATO_TID_FORMAT)),
                    "avsluttetDato" to henvendelse.avsluttetDato?.format(DateTimeFormatter.ofPattern(DATO_TID_FORMAT)),
                    "ferdigstiltDato" to melding.sendtDato.format(DateTimeFormatter.ofPattern(DATO_TID_FORMAT)),
                    "kontorsperretEnhet" to kontorsperretEnhet,
                    "kontorsperretAv" to kontorsperretAv,
                    "sendtTilSladding" to (henvendelse.sladding ?: false),
                    "markertSomFeilsendtAv" to markertSomFeilsendtAv
                )
            )
        }
        return TraadDTO(
            traadId = henvendelse.kjedeId,
            meldinger = meldinger,
            journalposter = journalposter
        )
    }

    private fun DialogMappingContext.tilJournalpostDTO(journalpost: JournalpostDTO) = DialogApi.Journalpost(
        journalfortDato = journalpost.journalfortDato,
        journalfortTema = journalpost.journalfortTema,
        journalfortTemanavn = temakodeMap[journalpost.journalfortTema] ?: journalpost.journalfortTema,
        journalfortSaksid = journalpost.fagsakId,
        journalfortAv = identMap[journalpost.journalforerNavIdent ?: ""]
            ?.let {
                DialogApi.Veileder(
                    ident = it.ident,
                    navn = "${it.fornavn} ${it.etternavn}"
                )
            }
            ?: DialogApi.Veileder.UKJENT,
    )

    private fun hentFritekstFraMelding(
        erKassert: Boolean,
        melding: no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.models.MeldingDTO
    ): String {
        if (erKassert) {
            return "Innholdet i denne henvendelsen er slettet av NAV."
        }
        return melding.fritekst ?: "Innholdet i denne henvendelsen er ikke tilgjengelig."
    }

    private fun meldingstypeFraSfTyper(
        henvendelse: HenvendelseDTO,
        melding: no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.models.MeldingDTO
    ): Meldingstype {
        val erForsteMelding = henvendelse.meldinger?.firstOrNull() == melding
        return when (henvendelse.henvendelseType) {
            HenvendelseDTO.HenvendelseType.SAMTALEREFERAT -> {
                when (melding.kanal) {
                    Kanal.OPPMOTE -> Meldingstype.SAMTALEREFERAT_OPPMOTE
                    Kanal.TELEFON -> Meldingstype.SAMTALEREFERAT_TELEFON
                    Kanal.DIGITAL -> Meldingstype.SAMTALEREFERAT_TELEFON // Har ikke tilsvarende i gamle api.
                    else -> Meldingstype.SAMTALEREFERAT_TELEFON
                }
            }
            HenvendelseDTO.HenvendelseType.MELDINGSKJEDE -> {
                when (melding.fra.identType) {
                    MeldingFraDTO.IdentType.AKTORID -> if (erForsteMelding) Meldingstype.SPORSMAL_SKRIFTLIG else Meldingstype.SVAR_SBL_INNGAAENDE
                    MeldingFraDTO.IdentType.NAVIDENT -> if (erForsteMelding) Meldingstype.SPORSMAL_MODIA_UTGAAENDE else Meldingstype.SVAR_SKRIFTLIG
                    MeldingFraDTO.IdentType.SYSTEM -> Meldingstype.SVAR_SKRIFTLIG
                }
            }
            HenvendelseDTO.HenvendelseType.CHAT -> {
                when (melding.fra.identType) {
                    MeldingFraDTO.IdentType.AKTORID -> if (erForsteMelding) Meldingstype.SPORSMAL_SKRIFTLIG else Meldingstype.SVAR_SBL_INNGAAENDE
                    MeldingFraDTO.IdentType.NAVIDENT -> if (erForsteMelding) Meldingstype.SPORSMAL_MODIA_UTGAAENDE else Meldingstype.SVAR_SKRIFTLIG
                    MeldingFraDTO.IdentType.SYSTEM -> Meldingstype.SVAR_SKRIFTLIG
                }
            }
        }
    }

    private fun Meldingstype.getKanal(): SamtalereferatRequestDTO.Kanal {
        return when (this) {
            Meldingstype.SAMTALEREFERAT_OPPMOTE -> SamtalereferatRequestDTO.Kanal.OPPMOTE
            Meldingstype.SAMTALEREFERAT_TELEFON -> SamtalereferatRequestDTO.Kanal.TELEFON
            else -> throw IllegalArgumentException("Ikke støttet meldingstype, $this")
        }
    }
}

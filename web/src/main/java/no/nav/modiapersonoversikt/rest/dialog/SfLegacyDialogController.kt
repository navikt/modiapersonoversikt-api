package no.nav.modiapersonoversikt.rest.dialog

import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.legacy.api.domain.Oppgave
import no.nav.modiapersonoversikt.legacy.api.domain.Saksbehandler
import no.nav.modiapersonoversikt.legacy.api.domain.Temagruppe
import no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Meldingstype
import no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Status
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.models.*
import no.nav.modiapersonoversikt.legacy.api.service.OppgaveBehandlingService
import no.nav.modiapersonoversikt.legacy.api.service.kodeverk.StandardKodeverk
import no.nav.modiapersonoversikt.legacy.api.service.ldap.LDAPService
import no.nav.modiapersonoversikt.legacy.api.utils.RestUtils
import no.nav.modiapersonoversikt.legacy.api.utils.TemagruppeTemaMapping
import no.nav.modiapersonoversikt.rest.DATO_TID_FORMAT
import no.nav.modiapersonoversikt.service.sfhenvendelse.EksternBruker
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.format.DateTimeFormatter
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.NotSupportedException

@RestController
@RequestMapping("/rest/sf-legacy-dialog/{fnr}")
class SfLegacyDialogController @Autowired constructor(
    private val tilgangskontroll: Tilgangskontroll,
    private val sfHenvendelseService: SfHenvendelseService,
    private val oppgaveBehandlingService: OppgaveBehandlingService,
    private val ldapService: LDAPService,
    private val kodeverk: StandardKodeverk
) {
    @GetMapping("/meldinger")
    fun hentMeldinger(
        request: HttpServletRequest,
        @PathVariable("fnr") fnr: String,
        @RequestParam(value = "enhet", required = false) enhet: String?
    ): List<TraadDTO> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Henvendelse.Les, AuditIdentifier.FNR to fnr)) {
                val valgtEnhet = RestUtils.hentValgtEnhet(enhet, request)
                val bruker = EksternBruker.Fnr(fnr)

                val sfHenvendelser = sfHenvendelseService.hentHenvendelser(bruker, valgtEnhet)
                val (temakodeMap, identMap) = lagOppslagsverk(sfHenvendelser)
                sfHenvendelser
                    .map {
                        mapSfHenvendelserTilLegacyFormat(
                            temakodeMap = temakodeMap,
                            identMap = identMap,
                            henvendelse = it
                        )
                    }
            }
    }

    @PostMapping("/sendreferat")
    fun sendMelding(
        request: HttpServletRequest,
        @PathVariable("fnr") fnr: String,
        @RequestBody body: SendReferatRequest
    ): ResponseEntity<Void> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(Audit.Action.CREATE, AuditResources.Person.Henvendelse.Les, AuditIdentifier.FNR to fnr)) {
                sfHenvendelseService.sendSamtalereferat(
                    bruker = EksternBruker.Fnr(fnr),
                    enhet = RestUtils.hentValgtEnhet(null, request),
                    temagruppe = body.temagruppe,
                    kanal = body.meldingstype.getKanal(),
                    fritekst = body.fritekst
                )
                ResponseEntity(HttpStatus.OK)
            }
    }

    @PostMapping("/sendsporsmal")
    fun sendSporsmal(
        request: HttpServletRequest,
        @PathVariable("fnr") fnr: String,
        @RequestBody body: SendSporsmalRequest
    ): ResponseEntity<Void> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(Audit.Action.CREATE, AuditResources.Person.Henvendelse.Les, AuditIdentifier.FNR to fnr)) {
                sfHenvendelseService.opprettNyDialogOgSendMelding(
                    bruker = EksternBruker.Fnr(fnr),
                    enhet = RestUtils.hentValgtEnhet(body.enhet, request),
                    temagruppe = TemagruppeTemaMapping.hentTemagruppeForTema(body.sak.temaKode),
                    fritekst = body.fritekst
                )
                // Ja, sak blir sendt inn. Men vi kan ikke journalføre henvendelsen her siden den da blir lukket, og bruker kan ikke svare.
                // TODO Hvordan vil vi håndtere dette?? Hvordan blir dette med tilgangskontroll på tema?
                ResponseEntity(HttpStatus.OK)
            }
    }

    @PostMapping("/sendinfomelding")
    fun sendInfomelding(
        request: HttpServletRequest,
        @PathVariable("fnr") fnr: String,
        @RequestBody body: InfomeldingRequest
    ): ResponseEntity<Void> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(Audit.Action.CREATE, AuditResources.Person.Henvendelse.Les, AuditIdentifier.FNR to fnr)) {
                val enhet = RestUtils.hentValgtEnhet(body.enhet, request)
                sfHenvendelseService.opprettNyDialogOgSendMelding(
                    bruker = EksternBruker.Fnr(fnr),
                    enhet = enhet,
                    temagruppe = TemagruppeTemaMapping.hentTemagruppeForTema(body.sak.temaKode),
                    fritekst = body.fritekst
                )

                // Direkte journalforing slik at dialog lukkes for svar fra bruker
                // TODO Hvordan vil vi håndtere dette??
                sfHenvendelseService.journalforHenvendelse(
                    enhet = enhet,
                    henvendelseId = "???",
                    saksId = body.sak.saksId,
                    saksTema = body.sak.temaKode
                )
                ResponseEntity(HttpStatus.OK)
            }
    }

    @PostMapping("/fortsett/opprett")
    fun startFortsettDialog(
        request: HttpServletRequest,
        @PathVariable("fnr") fnr: String,
        @RequestHeader(value = "Ignore-Conflict", required = false) ignorerConflict: Boolean?,
        @RequestBody body: OpprettHenvendelseRequest
    ): FortsettDialogDTO {
        val auditIdentifier = arrayOf(
            AuditIdentifier.FNR to fnr,
            AuditIdentifier.TRAAD_ID to body.traadId
        )
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(Audit.Action.CREATE, AuditResources.Person.Henvendelse.Opprettet, *auditIdentifier)) {
                /**
                 * Artifakt av legacy-henvendelse, beholdt for å holde apiene like.
                 */
                FortsettDialogDTO(body.traadId, null)
            }
    }

    @PostMapping("/fortsett/ferdigstill")
    fun sendFortsettDialog(
        request: HttpServletRequest,
        @PathVariable("fnr") fnr: String,
        @RequestBody body: FortsettDialogRequest
    ): ResponseEntity<Void> {
        val auditIdentifier = arrayOf(
            AuditIdentifier.FNR to fnr,
            AuditIdentifier.BEHANDLING_ID to body.behandlingsId,
            AuditIdentifier.OPPGAVE_ID to body.oppgaveId
        )
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(Audit.Action.UPDATE, AuditResources.Person.Henvendelse.Ferdigstill, *auditIdentifier)) {
                val kjedeId = body.traadId
                val oppgaveId = body.oppgaveId

                val bruker = EksternBruker.Fnr(fnr)
                val enhet = RestUtils.hentValgtEnhet(body.enhet, request)
                val henvendelse = sfHenvendelseService.hentHenvendelse(body.traadId)

                val henvendelseTilhorerBruker = sfHenvendelseService.sjekkEierskap(bruker, henvendelse)
                if (!henvendelseTilhorerBruker) {
                    throw ResponseStatusException(HttpStatus.FORBIDDEN, "Henvendelse $kjedeId tilhørte ikke bruker")
                }

                if (oppgaveId != null) {
                    val oppgave: Oppgave? = oppgaveBehandlingService.hentOppgave(oppgaveId)
                    if (body.traadId != oppgave?.henvendelseId) {
                        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Feil oppgaveId fra client. Forventet '${body.traadId}', men fant '${oppgave?.henvendelseId}'")
                    } else if (oppgaveBehandlingService.oppgaveErFerdigstilt(oppgaveId)) {
                        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Feil oppgaveId fra client. Oppgaven er allerede ferdigstilt")
                    }
                }

                sfHenvendelseService.sendMeldingPaEksisterendeDialog(
                    bruker = bruker,
                    kjedeId = body.traadId,
                    enhet = enhet,
                    fritekst = body.fritekst
                )

                if (oppgaveId != null) {
                    oppgaveBehandlingService.ferdigstillOppgaveIGsak(
                        oppgaveId,
                        Temagruppe.valueOf(henvendelse.gjeldendeTemagruppe),
                        enhet
                    )
                }

                ResponseEntity(HttpStatus.OK)
            }
    }

    @PostMapping("slaasammen")
    fun slaaSammenTraader(
        request: HttpServletRequest,
        @PathVariable("fnr") fnr: String,
        @RequestBody slaaSammenRequest: SlaaSammenRequest
    ): Map<String, Any?> {
        val auditIdentifier = arrayOf(
            AuditIdentifier.FNR to fnr,
            AuditIdentifier.TRAAD_ID to slaaSammenRequest.traader.joinToString(" ") {
                "${it.traadId}:${it.oppgaveId}"
            }
        )
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(Audit.Action.UPDATE, AuditResources.Person.Henvendelse.SlaSammen, *auditIdentifier)) {
                throw NotSupportedException("Operasjonen er ikke støttet av Salesforce")
            }
    }

    private fun lagOppslagsverk(henvendelser: List<HenvendelseDTO>): Pair<Map<String, String>, Map<String, Saksbehandler>> {
        val temakoder = henvendelser
            .flatMap { it.journalposter ?: emptyList() }
            .map { it.journalfortTema }
            .distinct()
        val identer = henvendelser
            .flatMap { henvendelse ->
                val journalportIdenter: List<String>? = henvendelse.journalposter?.map { it.journalforerNavIdent }
                val markeringIdenter: List<String>? = henvendelse.markeringer?.map { it.markertAv }
                val meldingFraIdenter: List<String>? = henvendelse.meldinger
                    ?.filter { it.fra.identType == MeldingFraDTO.IdentType.AKTORID }
                    ?.map { it.fra.ident }
                (journalportIdenter ?: emptyList())
                    .plus(markeringIdenter ?: emptyList())
                    .plus(meldingFraIdenter ?: emptyList())
            }
            .distinct()

        val temakodeMap = temakoder.associateWith { kode -> kodeverk.getArkivtemaNavn(kode) }
        val identMap = identer.associateWith { ident -> ldapService.hentSaksbehandler(ident) }

        return Pair(temakodeMap, identMap)
    }

    private fun mapSfHenvendelserTilLegacyFormat(
        temakodeMap: Map<String, String>,
        identMap: Map<String, Saksbehandler>,
        henvendelse: HenvendelseDTO
    ): TraadDTO {
        val journalpost: JournalpostDTO? = henvendelse.journalposter?.firstOrNull()
        val meldinger: List<MeldingDTO> = (henvendelse.meldinger ?: emptyList()).map { melding ->
            MeldingDTO(
                mapOf(
                    "id" to "${henvendelse.kjedeId}-${(henvendelse.hashCode())}",
                    "oppgaveId" to null,
                    "meldingstype" to meldingstypeFraSfTyper(henvendelse, melding),
                    "temagruppe" to henvendelse.gjeldendeTemagruppe,
                    "skrevetAvTekst" to (
                        identMap[melding.fra.ident]
                            ?.let { "${it.navn} (${it.ident})" } ?: "Ukjent"
                        ),
                    "journalfortAv" to identMap[journalpost?.journalforerNavIdent]
                        ?.let { mapOf("fornavn" to it.fornavn, "etternavn" to it.etternavn) },
                    "journalfortDato" to journalpost?.journalfortDato?.format(DateTimeFormatter.ofPattern(DATO_TID_FORMAT)),
                    "journalfortTema" to journalpost?.journalfortTema,
                    "journalfortTemanavn" to temakodeMap[journalpost?.journalfortTema],
                    "journalfortSaksid" to journalpost?.journalpostId,
                    "fritekst" to melding.fritekst,
                    "lestDato" to melding.lestDato?.format(DateTimeFormatter.ofPattern(DATO_TID_FORMAT)),
                    "status" to when {
                        melding.fra.identType == MeldingFraDTO.IdentType.AKTORID -> Status.IKKE_BESVART
                        melding.lestDato != null -> Status.LEST_AV_BRUKER
                        else -> Status.IKKE_LEST_AV_BRUKER
                    },
                    "statusTekst" to null, // Blir ikke brukt av frontend uansett
                    "opprettetDato" to melding.sendtDato.format(DateTimeFormatter.ofPattern(DATO_TID_FORMAT)),
                    "ferdigstiltDato" to melding.sendtDato.format(DateTimeFormatter.ofPattern(DATO_TID_FORMAT)),
                    "erFerdigstiltUtenSvar" to false, // TODO Informasjon finnes ikke i SF
                    "ferdigstiltUtenSvarDato" to null, // TODO Informasjon finnes ikke i SF
                    "ferdigstiltUtenSvarAv" to null, // TODO Informasjon finnes ikke i SF
                    "kontorsperretEnhet" to if (henvendelse.kontorsperre) henvendelse.opprinneligGT else null,
                    "kontorsperretAv" to henvendelse.markeringer
                        ?.find { it.markeringstype == MarkeringDTO.Markeringstype.KONTORSPERRE }
                        ?.markertAv
                        ?.let { identMap[it] }
                        ?.let { mapOf("fornavn" to it.fornavn, "etternavn" to it.etternavn, "ident" to it.ident) },
                    "markertSomFeilsendtAv" to henvendelse.markeringer
                        ?.find { it.markeringstype == MarkeringDTO.Markeringstype.FEILSENDT }
                        ?.markertAv
                        ?.let { identMap[it] }
                        ?.let { mapOf("fornavn" to it.fornavn, "etternavn" to it.etternavn, "ident" to it.ident) },
                    "erDokumentMelding" to false // Brukes ikke
                )
            )
        }
        return TraadDTO(requireNotNull(henvendelse.kjedeId), meldinger)
    }

    private fun meldingstypeFraSfTyper(henvendelse: HenvendelseDTO, melding: no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.models.MeldingDTO): Meldingstype {
        val erForsteMelding = henvendelse.meldinger?.firstOrNull() == melding
        return when (henvendelse.henvendelseType) {
            HenvendelseDTO.HenvendelseType.SAMTALEREFERAT -> Meldingstype.SAMTALEREFERAT_TELEFON // TODO trenger kanal fra SF her
            HenvendelseDTO.HenvendelseType.MELDINGSKJEDE -> {
                when (melding.fra.identType) {
                    MeldingFraDTO.IdentType.AKTORID -> if (erForsteMelding) Meldingstype.SPORSMAL_SKRIFTLIG else Meldingstype.SVAR_SBL_INNGAAENDE
                    MeldingFraDTO.IdentType.NAVIDENT -> if (erForsteMelding) Meldingstype.SPORSMAL_MODIA_UTGAAENDE else Meldingstype.SVAR_SKRIFTLIG
                }
            }
        }
    }

    fun Meldingstype.getKanal(): SamtalereferatRequestDTO.Kanal {
        return when (this) {
            Meldingstype.SAMTALEREFERAT_OPPMOTE -> SamtalereferatRequestDTO.Kanal.OPPMOTE
            Meldingstype.SAMTALEREFERAT_TELEFON -> SamtalereferatRequestDTO.Kanal.TELEFON
            else -> throw IllegalArgumentException("Ikke støttet meldingstype, $this")
        }
    }
}

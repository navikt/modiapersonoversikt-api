package no.nav.modiapersonoversikt.service.dialog

import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.commondomain.Temagruppe
import no.nav.modiapersonoversikt.commondomain.Veileder
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.*
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.MeldingDTO.*
import no.nav.modiapersonoversikt.rest.dialog.domain.Meldingstype
import no.nav.modiapersonoversikt.rest.dialog.domain.Status
import no.nav.modiapersonoversikt.rest.dialog.domain.TraadType
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.enhetligkodeverk.KodeverkConfig
import no.nav.modiapersonoversikt.service.oppgavebehandling.Oppgave
import no.nav.modiapersonoversikt.service.oppgavebehandling.OppgaveBehandlingService
import no.nav.modiapersonoversikt.service.sfhenvendelse.EksternBruker
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.time.OffsetDateTime

class DialogServiceImpl(
    private val sfHenvendelseService: SfHenvendelseService,
    private val oppgaveBehandlingService: OppgaveBehandlingService,
    private val ansattService: AnsattService,
    private val kodeverk: EnhetligKodeverk.Service,
) : DialogService {
    override fun hentMeldinger(
        fnr: String,
        enhet: String,
    ): List<TraadDTO> {
        val bruker = EksternBruker.Fnr(fnr)

        val sfHenvendelser = sfHenvendelseService.hentHenvendelser(bruker, enhet)
        val dialogMappingContext = lagMappingContext(sfHenvendelser)

        return sfHenvendelser.map { dialogMappingContext.mapSfHenvendelserTilLegacyFormat(it) }
    }

    override fun sendMelding(
        fnr: String,
        meldingRequest: SendMelding,
    ): TraadDTO =
        if (meldingRequest.traadId != null) {
            fortsettPaEksisterendeDialog(fnr, meldingRequest)
        } else {
            opprettNyDialog(fnr, meldingRequest)
        }

    override fun sendInfomelding(
        fnr: String,
        infomeldingRequest: Infomelding,
    ): TraadDTO {
        val temagruppe = DialogTemagruppeTemaMapping.hentTemagruppeForTema(infomeldingRequest.sak.temaKode)
        val henvendelse =
            sfHenvendelseService.opprettNyDialogOgSendMelding(
                bruker = EksternBruker.Fnr(fnr),
                enhet = infomeldingRequest.enhet,
                temagruppe = temagruppe,
                tilknyttetAnsatt = false,
                fritekst = infomeldingRequest.fritekst,
            )

        sfHenvendelseService.lukkTraad(henvendelse.kjedeId)
        sfHenvendelseService.journalforHenvendelse(
            enhet = infomeldingRequest.enhet,
            kjedeId = henvendelse.kjedeId,
            saksId = infomeldingRequest.sak.fagsystemSaksId,
            saksTema = infomeldingRequest.sak.temaKode,
            fagsakSystem = infomeldingRequest.sak.fagsystemKode,
        )

        return parseFraHenvendelseTilTraad(henvendelse)
    }

    override fun startFortsettDialog(
        fnr: String,
        ignorerConflict: Boolean?,
        opprettHenvendelseRequest: OpprettHenvendelse,
    ): FortsettDialogDTO {
        /**
         * Artifakt av legacy-henvendelse, beholdt for å holde apiene like.
         */
        val traad = sfHenvendelseService.hentHenvendelse(opprettHenvendelseRequest.traadId)
        val oppgaveId: String? =
            finnOgTilordneOppgaveIdTilTrad(traad, fnr, opprettHenvendelseRequest.enhet, ignorerConflict == true)

        return FortsettDialogDTO(opprettHenvendelseRequest.traadId, oppgaveId)
    }

    private fun parseFraHenvendelseTilTraad(henvendelse: HenvendelseDTO): TraadDTO {
        val dialogMappingContext = lagMappingContext(listOf(henvendelse))
        return dialogMappingContext.mapSfHenvendelserTilLegacyFormat(henvendelse)
    }

    private fun finnOgTilordneOppgaveIdTilTrad(
        traad: HenvendelseDTO,
        fnr: String,
        enhet: String?,
        ignorerConflict: Boolean,
    ): String? =
        if (traad.erSporsmalFraBruker()) {
            try {
                oppgaveBehandlingService
                    .finnOgTilordneSTOOppgave(
                        fnr,
                        traad.kjedeId,
                        traad.gjeldendeTemagruppe?.let { Temagruppe.valueOf(it) },
                        enhet,
                        ignorerConflict,
                    )?.oppgaveId
            } catch (e: OppgaveBehandlingService.AlleredeTildeltAnnenSaksbehandler) {
                throw ResponseStatusException(HttpStatus.CONFLICT, e.message)
            }
        } else {
            null
        }

    private fun HenvendelseDTO.erSporsmalFraBruker(): Boolean {
        val nyesteMelding = this.meldinger?.maxByOrNull { it.sendtDato }
        return nyesteMelding?.fra?.identType == MeldingFraDTO.IdentType.AKTORID
    }

    class DialogMappingContext(
        val temakodeMap: Map<String, String>,
        val identMap: Map<String, Veileder>,
    ) {
        fun getVeileder(ident: String?): Veileder? = identMap[ident ?: ""]
    }

    private fun lagMappingContext(henvendelser: List<HenvendelseDTO>): DialogMappingContext {
        val temakodeMap = kodeverk.hentKodeverk(KodeverkConfig.ARKIVTEMA).asMap()
        val identer =
            henvendelser
                .flatMap { henvendelse ->
                    // TODO SF bør ikke svare med null for ident her. Rapportert feil
                    val journalportIdenter: List<String>? =
                        henvendelse.journalposter?.mapNotNull { it.journalforerNavIdent }
                    val markeringIdenter: List<String>? = henvendelse.markeringer?.mapNotNull { it.markertAv }
                    val meldingFraIdenter: List<String>? =
                        henvendelse.meldinger
                            ?.filter { it.fra.identType == MeldingFraDTO.IdentType.NAVIDENT }
                            ?.mapNotNull { it.fra.ident }
                    (journalportIdenter ?: emptyList())
                        .plus(markeringIdenter ?: emptyList())
                        .plus(meldingFraIdenter ?: emptyList())
                }.distinct()
                .map(::NavIdent)

        val identMap = ansattService.hentVeiledere(identer).mapKeys { (key, _) -> key.get() }

        return DialogMappingContext(temakodeMap, identMap)
    }

    private fun DialogMappingContext.mapSfHenvendelserTilLegacyFormat(henvendelse: HenvendelseDTO): TraadDTO {
        val journalposter =
            henvendelse.journalposter
                ?.map { tilJournalpostDTO(it) }
                ?: emptyList()

        val markertSomFeilsendt = henvendelse.markeringer.getForType(MarkeringDTO.Markeringstype.FEILSENDT)
        val markertSomFeilsendtAv = getVeileder(markertSomFeilsendt?.markertAv)

        val henvendelseErKassert: Boolean = henvendelse.kasseringsDato?.isBefore(OffsetDateTime.now()) == true
        val meldinger: List<MeldingDTO> =
            (henvendelse.meldinger ?: emptyList()).map { melding ->
                val skrevetAv = getIdent(melding.fra.ident, melding.fra.identType)
                val status =
                    when {
                        melding.fra.identType == MeldingFraDTO.IdentType.AKTORID -> Status.IKKE_BESVART
                        melding.lestDato != null -> Status.LEST_AV_BRUKER
                        else -> Status.IKKE_LEST_AV_BRUKER
                    }

                MeldingDTO(
                    id = "${henvendelse.kjedeId}---${(melding.hashCode())}",
                    meldingsId = melding.meldingsId,
                    meldingstype = meldingstypeFraSfTyper(henvendelse, melding),
                    temagruppe = requireNotNull(henvendelse.gjeldendeTemagruppe), // TODO error in api-spec
                    skrevetAvTekst = skrevetAv ?: "",
                    fritekst = hentFritekstFraMelding(henvendelseErKassert, melding),
                    lestDato = melding.lestDato,
                    status = status,
                    opprettetDato = melding.sendtDato,
                    avsluttetDato = henvendelse.avsluttetDato,
                    ferdigstiltDato = melding.sendtDato,
                    sendtTilSladding = (henvendelse.sladding == true),
                    markertSomFeilsendtAv = markertSomFeilsendtAv,
                )
            }

        return TraadDTO(
            traadId = henvendelse.kjedeId,
            fnr = henvendelse.fnr,
            opprettetDato = henvendelse.opprettetDato,
            feilsendt = henvendelse.feilsendt,
            avsluttetDato = henvendelse.avsluttetDato,
            sladding = henvendelse.sladding,
            temagruppe = requireNotNull(henvendelse.gjeldendeTemagruppe),
            traadType = TraadType.valueOf(henvendelse.henvendelseType.value),
            meldinger = meldinger,
            journalposter = journalposter,
        )
    }

    private fun List<MarkeringDTO>?.getForType(type: MarkeringDTO.Markeringstype): MarkeringDTO? = this?.find { it.markeringstype == type }

    private fun DialogMappingContext.getIdent(
        ident: String?,
        identType: MeldingFraDTO.IdentType,
    ) = when (identType) {
        MeldingFraDTO.IdentType.NAVIDENT, MeldingFraDTO.IdentType.AKTORID ->
            getVeileder(ident)
                ?.let { "${it.navn} (${it.ident})" }
                ?: ident?.let { "($ident)" }

        MeldingFraDTO.IdentType.SYSTEM -> "Salesforce system"
    }

    private fun DialogMappingContext.tilJournalpostDTO(journalpost: JournalpostDTO) =
        DialogService.Journalpost(
            journalfortDato = journalpost.journalfortDato,
            journalfortTema = journalpost.journalfortTema,
            journalfortTemanavn = temakodeMap[journalpost.journalfortTema] ?: journalpost.journalfortTema,
            journalfortSaksid = journalpost.fagsakId,
            journalfortAv =
                identMap[journalpost.journalforerNavIdent ?: ""]
                    ?.let {
                        DialogService.Veileder(
                            ident = it.ident,
                            navn = "${it.fornavn} ${it.etternavn}",
                        )
                    }
                    ?: DialogService.Veileder.UKJENT,
            journalforendeEnhet = journalpost.journalforendeEnhet,
            journalfortFagsaksystem = journalpost.fagsaksystem?.value,
        )

    private fun hentFritekstFraMelding(
        erKassert: Boolean,
        melding: no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.MeldingDTO,
    ): String {
        if (erKassert) {
            return "Innholdet i denne henvendelsen er slettet av NAV."
        }
        return melding.fritekst ?: "Innholdet i denne henvendelsen er ikke tilgjengelig."
    }

    private fun meldingstypeFraSfTyper(
        henvendelse: HenvendelseDTO,
        melding: no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.MeldingDTO,
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
                    MeldingFraDTO.IdentType.AKTORID ->
                        if (erForsteMelding) {
                            Meldingstype.SPORSMAL_SKRIFTLIG
                        } else {
                            Meldingstype.SVAR_SBL_INNGAAENDE
                        }
                    MeldingFraDTO.IdentType.NAVIDENT ->
                        if (erForsteMelding) {
                            Meldingstype.SPORSMAL_MODIA_UTGAAENDE
                        } else {
                            Meldingstype.SVAR_SKRIFTLIG
                        }
                    MeldingFraDTO.IdentType.SYSTEM -> Meldingstype.SVAR_SKRIFTLIG
                }
            }

            HenvendelseDTO.HenvendelseType.CHAT -> {
                when (melding.fra.identType) {
                    MeldingFraDTO.IdentType.AKTORID -> Meldingstype.CHATMELDING_FRA_BRUKER
                    MeldingFraDTO.IdentType.NAVIDENT -> Meldingstype.CHATMELDING_FRA_NAV
                    MeldingFraDTO.IdentType.SYSTEM -> Meldingstype.CHATMELDING_FRA_NAV
                }
            }
        }
    }

    private fun opprettNyDialog(
        fnr: String,
        meldingRequest: SendMelding,
    ): TraadDTO {
        if (meldingRequest.traadType == TraadType.SAMTALEREFERAT) {
            val henvendelse =
                sfHenvendelseService.sendSamtalereferat(
                    kjedeId = null,
                    bruker = EksternBruker.Fnr(fnr),
                    enhet = meldingRequest.enhet,
                    temagruppe = meldingRequest.temagruppe!!,
                    kanal = SamtalereferatRequestDTO.Kanal.OPPMOTE,
                    fritekst = meldingRequest.fritekst,
                )

            return parseFraHenvendelseTilTraad(henvendelse)
        } else {
            val sak = meldingRequest.sak!!
            val temagruppe = DialogTemagruppeTemaMapping.hentTemagruppeForTema(sak.temaKode)

            val henvendelse =
                sfHenvendelseService.opprettNyDialogOgSendMelding(
                    bruker = EksternBruker.Fnr(fnr),
                    enhet = meldingRequest.enhet,
                    temagruppe = temagruppe,
                    tilknyttetAnsatt = meldingRequest.erOppgaveTilknyttetAnsatt!!,
                    fritekst = meldingRequest.fritekst,
                )

            if (meldingRequest.avsluttet == true) {
                sfHenvendelseService.lukkTraad(henvendelse.kjedeId)
            }

            sfHenvendelseService.journalforHenvendelse(
                enhet = meldingRequest.enhet,
                kjedeId = henvendelse.kjedeId,
                saksId = sak.fagsystemSaksId,
                saksTema = sak.temaKode,
                fagsakSystem = sak.fagsystemKode,
            )

            return parseFraHenvendelseTilTraad(henvendelse)
        }
    }

    override fun fortsettPaEksisterendeDialog(
        fnr: String,
        meldingRequest: SendMelding,
    ): TraadDTO {
        val kjedeId = meldingRequest.traadId!!
        val oppgaveId = meldingRequest.oppgaveId

        val bruker = EksternBruker.Fnr(fnr)
        val enhet = meldingRequest.enhet
        var henvendelse = sfHenvendelseService.hentHenvendelse(kjedeId)

        val henvendelseTilhorerBruker = sfHenvendelseService.sjekkEierskap(bruker, henvendelse)
        if (!henvendelseTilhorerBruker) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Henvendelse $kjedeId tilhørte ikke bruker")
        }

        if (oppgaveId != null) {
            val oppgave: Oppgave? = oppgaveBehandlingService.hentOppgave(oppgaveId)
            if (kjedeId != oppgave?.henvendelseId) {
                throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Feil oppgaveId fra client. Forventet '$kjedeId', men fant '${oppgave?.henvendelseId}'",
                )
            } else if (oppgaveBehandlingService.oppgaveErFerdigstilt(oppgaveId)) {
                throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Feil oppgaveId fra client. Oppgaven er allerede ferdigstilt",
                )
            }
        }

        if (meldingRequest.traadType == TraadType.SAMTALEREFERAT) {
            henvendelse =
                sfHenvendelseService.sendSamtalereferat(
                    kjedeId = kjedeId,
                    bruker = bruker,
                    enhet = enhet,
                    temagruppe = henvendelse.gjeldendeTemagruppe!!,
                    kanal = SamtalereferatRequestDTO.Kanal.OPPMOTE,
                    fritekst = meldingRequest.fritekst,
                )

            val journalposter =
                (henvendelse.journalposter ?: emptyList())
                    .distinctBy { it.fagsakId }
            journalposter.forEach {
                sfHenvendelseService.journalforHenvendelse(
                    enhet = enhet,
                    kjedeId = henvendelse.kjedeId,
                    saksId = it.fagsakId,
                    saksTema = it.journalfortTema,
                    fagsakSystem = it.fagsaksystem?.name,
                )
            }
        } else {
            val sak = meldingRequest.sak
            henvendelse =
                sfHenvendelseService.sendMeldingPaEksisterendeDialog(
                    bruker = bruker,
                    kjedeId = kjedeId,
                    enhet = enhet,
                    tilknyttetAnsatt = meldingRequest.erOppgaveTilknyttetAnsatt!!,
                    fritekst = meldingRequest.fritekst,
                )

            if (meldingRequest.avsluttet == true) {
                sfHenvendelseService.lukkTraad(henvendelse.kjedeId)
            }

            if (sak != null) {
                sfHenvendelseService.journalforHenvendelse(
                    enhet = enhet,
                    kjedeId = henvendelse.kjedeId,
                    saksId = sak.fagsystemSaksId,
                    saksTema = sak.temaKode,
                    fagsakSystem = sak.fagsystemKode,
                )
            }
        }
        if (oppgaveId != null) {
            oppgaveBehandlingService.ferdigstillOppgaveIGsak(
                oppgaveId,
                Temagruppe.valueOf(henvendelse.gjeldendeTemagruppe!!),
                enhet,
            )
        }

        return parseFraHenvendelseTilTraad(henvendelse)
    }
}

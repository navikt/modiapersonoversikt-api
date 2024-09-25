package no.nav.modiapersonoversikt.rest.dialog.apis

import no.nav.modiapersonoversikt.commondomain.Veileder
import no.nav.modiapersonoversikt.rest.dialog.domain.Meldingstype
import no.nav.modiapersonoversikt.rest.dialog.domain.Status
import no.nav.modiapersonoversikt.rest.dialog.domain.TraadType
import no.nav.modiapersonoversikt.service.journalforingsaker.JournalforingSak
import java.time.OffsetDateTime

interface DialogApi {
    fun hentMeldinger(
        fnr: String,
        enhet: String,
    ): List<TraadDTO>

    fun sendMelding(
        fnr: String,
        meldingRequest: SendMelding,
    ): TraadDTO

    fun sendInfomelding(
        fnr: String,
        infomeldingRequest: Infomelding,
    ): TraadDTO

    fun startFortsettDialog(
        fnr: String,
        ignorerConflict: Boolean?,
        opprettHenvendelseRequest: OpprettHenvendelse,
    ): FortsettDialogDTO

    fun fortsettPaEksisterendeDialog(
        fnr: String,
        meldingRequest: SendMelding,
    ): TraadDTO

    data class Journalpost(
        val journalfortAv: Veileder?,
        val journalfortDato: OffsetDateTime,
        val journalfortTema: String,
        val journalfortTemanavn: String,
        val journalfortSaksid: String?,
        val journalforendeEnhet: String?,
        val journalfortFagsaksystem: String?,
    )

    data class Veileder(
        val ident: String,
        val navn: String,
    ) {
        companion object {
            val UKJENT = Veileder("-", "Ukjent")
        }
    }
}

data class TraadDTO(
    val traadId: String,
    val fnr: String,
    val traadType: TraadType,
    val temagruppe: String,
    val opprettetDato: OffsetDateTime?,
    val kontorsperre: Boolean,
    val feilsendt: Boolean,
    val avsluttetDato: OffsetDateTime?,
    val sistEndretAv: String?,
    val sladding: Boolean?,
    val lukketAv: String?,
    val meldinger: List<MeldingDTO>,
    val journalposter: List<DialogApi.Journalpost>,
)

data class MeldingDTO(
    val id: String,
    val meldingsId: String?,
    val meldingstype: Meldingstype,
    val temagruppe: String,
    val skrevetAvTekst: String,
    val fritekst: String,
    val lestDato: OffsetDateTime?,
    val status: Status,
    val opprettetDato: OffsetDateTime,
    val avsluttetDato: OffsetDateTime?,
    val ferdigstiltDato: OffsetDateTime,
    val kontorsperretEnhet: String?,
    val kontorsperretAv: Veileder?,
    val sendtTilSladding: Boolean,
    val markertSomFeilsendtAv: Veileder?,
)

class FortsettDialogDTO(
    val behandlingsId: String,
    val oppgaveId: String?,
)

abstract class OpprettHenvendelse {
    abstract val enhet: String?
    abstract val traadId: String
}

data class OpprettHenvendelseRequest(
    override val enhet: String?,
    override val traadId: String,
) : OpprettHenvendelse()

data class OpprettHenvendelseRequestV2(
    val fnr: String,
    override val enhet: String?,
    override val traadId: String,
) : OpprettHenvendelse()

abstract class SendMelding {
    abstract val traadId: String?
    abstract val traadType: TraadType
    abstract val enhet: String
    abstract val fritekst: String
    abstract val temagruppe: String?
    abstract val sak: JournalforingSak?
    abstract val erOppgaveTilknyttetAnsatt: Boolean?
    abstract val avsluttet: Boolean?
    abstract val behandlingsId: String?
    abstract val oppgaveId: String?
}

data class SendMeldingRequest(
    override val traadId: String?,
    override val traadType: TraadType,
    override val enhet: String,
    override val fritekst: String,
    override val temagruppe: String?,
    override val sak: JournalforingSak?,
    override val erOppgaveTilknyttetAnsatt: Boolean?,
    override val avsluttet: Boolean?,
    override val behandlingsId: String?,
    override val oppgaveId: String?,
) : SendMelding()

data class SendMeldingRequestV2(
    val fnr: String,
    override val traadId: String?,
    override val traadType: TraadType,
    override val enhet: String,
    override val fritekst: String,
    override val temagruppe: String?,
    override val sak: JournalforingSak?,
    override val erOppgaveTilknyttetAnsatt: Boolean?,
    override val avsluttet: Boolean?,
    override val behandlingsId: String?,
    override val oppgaveId: String?,
) : SendMelding()

abstract class Infomelding {
    abstract val enhet: String
    abstract val fritekst: String
    abstract val sak: JournalforingSak
}

data class InfomeldingRequest(
    override val enhet: String,
    override val fritekst: String,
    override val sak: JournalforingSak,
) : Infomelding()

data class InfomeldingRequestV2(
    val fnr: String,
    override val enhet: String,
    override val fritekst: String,
    override val sak: JournalforingSak,
) : Infomelding()

package no.nav.modiapersonoversikt.rest.dialog.apis

import no.nav.modiapersonoversikt.commondomain.Temagruppe
import no.nav.modiapersonoversikt.commondomain.Veileder
import no.nav.modiapersonoversikt.rest.dialog.domain.Meldingstype
import no.nav.modiapersonoversikt.rest.dialog.domain.Status
import no.nav.modiapersonoversikt.rest.dialog.domain.TraadType
import no.nav.modiapersonoversikt.service.journalforingsaker.JournalforingSak
import java.time.OffsetDateTime

interface DialogApi {
    fun hentMeldinger(
        fnr: String,
        enhet: String
    ): List<TraadDTO>

    fun sendMelding(
        fnr: String,
        referatRequest: SendReferatRequest
    ): TraadDTO

    fun sendSporsmal(
        fnr: String,
        sporsmalsRequest: SendSporsmalRequest
    ): TraadDTO

    fun sendInfomelding(
        fnr: String,
        infomeldingRequest: InfomeldingRequest
    ): TraadDTO

    fun startFortsettDialog(
        fnr: String,
        ignorerConflict: Boolean?,
        opprettHenvendelseRequest: OpprettHenvendelseRequest
    ): FortsettDialogDTO

    fun sendFortsettDialog(
        fnr: String,
        fortsettDialogRequest: FortsettDialogRequest
    ): TraadDTO

    fun slaaSammenTraader(
        fnr: String,
        slaaSammenRequest: SlaaSammenRequest
    ): Map<String, Any?>

    data class Journalpost(
        val journalfortAv: Veileder?,
        val journalfortDato: OffsetDateTime,
        val journalfortTema: String,
        val journalfortTemanavn: String,
        val journalfortSaksid: String?
    )

    data class Veileder(
        val ident: String,
        val navn: String
    ) {
        companion object {
            val UKJENT = Veileder("-", "Ukjent")
        }
    }
}

data class TraadDTO(
    val traadId: String,
    val traadType: TraadType,
    val meldinger: List<MeldingDTO>,
    val journalposter: List<DialogApi.Journalpost>
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
class FortsettDialogDTO(val behandlingsId: String, val oppgaveId: String?)

data class OpprettHenvendelseRequest(
    val enhet: String?,
    val traadId: String
)

data class SendReferatRequest(
    val enhet: String,
    val fritekst: String,
    val temagruppe: String,
    val meldingstype: Meldingstype
)

data class SendSporsmalRequest(
    val enhet: String,
    val fritekst: String,
    val sak: JournalforingSak,
    val erOppgaveTilknyttetAnsatt: Boolean
)

data class InfomeldingRequest(
    val enhet: String,
    val fritekst: String,
    val sak: JournalforingSak
)

data class FortsettDialogRequest(
    val enhet: String,
    val traadId: String,
    val behandlingsId: String,
    val fritekst: String,
    val sak: JournalforingSak?,
    val erOppgaveTilknyttetAnsatt: Boolean,
    val meldingstype: Meldingstype,
    val oppgaveId: String?
)

data class SlaaSammenRequest(
    val enhet: String?,
    val traader: List<SlaaSammenTraad>,
    val temagruppe: Temagruppe
)

data class SlaaSammenTraad(
    val oppgaveId: String,
    val traadId: String
)

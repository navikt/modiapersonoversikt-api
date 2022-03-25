package no.nav.modiapersonoversikt.rest.dialog.apis

import no.nav.modiapersonoversikt.legacy.api.domain.Temagruppe
import no.nav.modiapersonoversikt.rest.dialog.domain.Meldingstype
import no.nav.modiapersonoversikt.service.saker.Sak
import org.springframework.http.ResponseEntity
import java.time.OffsetDateTime
import java.util.HashMap
import javax.servlet.http.HttpServletRequest

interface DialogApi {
    fun hentMeldinger(
        request: HttpServletRequest,
        fnr: String,
        enhet: String?
    ): List<TraadDTO>

    fun sendMelding(
        request: HttpServletRequest,
        fnr: String,
        referatRequest: SendReferatRequest
    ): ResponseEntity<Void>

    fun sendSporsmal(
        request: HttpServletRequest,
        fnr: String,
        sporsmalsRequest: SendSporsmalRequest
    ): ResponseEntity<Void>

    fun sendInfomelding(
        request: HttpServletRequest,
        fnr: String,
        infomeldingRequest: InfomeldingRequest
    ): ResponseEntity<Void>

    fun startFortsettDialog(
        request: HttpServletRequest,
        fnr: String,
        ignorerConflict: Boolean?,
        opprettHenvendelseRequest: OpprettHenvendelseRequest
    ): FortsettDialogDTO

    fun sendFortsettDialog(
        request: HttpServletRequest,
        fnr: String,
        fortsettDialogRequest: FortsettDialogRequest
    ): ResponseEntity<Void>

    fun slaaSammenTraader(
        request: HttpServletRequest,
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

data class BehandlingsId(val behandlingsId: String)
data class TraadDTO(
    val traadId: String,
    val meldinger: List<MeldingDTO>,
    val journalposter: List<DialogApi.Journalpost>
)
class MeldingDTO(val map: Map<String, Any?>) : HashMap<String, Any?>(map)
class FortsettDialogDTO(val behandlingsId: String, val oppgaveId: String?)

data class OpprettHenvendelseRequest(
    val enhet: String?,
    val traadId: String
)

data class SendReferatRequest(
    val enhet: String?,
    val fritekst: String,
    val temagruppe: String,
    val meldingstype: Meldingstype
)

data class SendSporsmalRequest(
    val enhet: String?,
    val fritekst: String,
    val sak: Sak,
    val erOppgaveTilknyttetAnsatt: Boolean
)

data class InfomeldingRequest(
    val enhet: String?,
    val fritekst: String,
    val sak: Sak
)

data class FortsettDialogRequest(
    val enhet: String?,
    val traadId: String,
    val behandlingsId: String,
    val fritekst: String,
    val sak: Sak?,
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

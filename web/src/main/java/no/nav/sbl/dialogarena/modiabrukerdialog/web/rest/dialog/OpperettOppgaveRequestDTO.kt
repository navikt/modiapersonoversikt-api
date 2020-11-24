package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.dialog

data class OpperettOppgaveRequestDTO(
        val fnr: String,
        val opprettetavenhetsnummer: String,
        val valgtEnhetId: Int,
        val behandlingskjedeId: String,
        val dagerFrist: Int,
        val ansvarligEnhetId: String,
        val ansvarligIdent: String?,
        val beskrivelse: String,
        val temaKode: String,
        val underkategoriKode: String?,
        val brukerid: String,
        val oppgaveTypeKode: String,
        val prioritetKode: String
)
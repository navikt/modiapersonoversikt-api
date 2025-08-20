package no.nav.modiapersonoversikt.service.oppgavebehandling

data class Oppgave(
    val oppgaveId: String,
    val fnr: String,
    val henvendelseId: String?,
    val erSTOOppgave: Boolean,
    val tildeltEnhetsnr: String,
    val tema: String,
    val temagruppe: String? = null,
    val oppgavetype: String,
    val prioritet: String,
    val status: String,
    val aktivDato: java.time.LocalDate,
    val id: Long? = null,
    val endretAvEnhetsnr: String? = null,
    val opprettetAvEnhetsnr: String? = null,
    val saksreferanse: String? = null,
    val beskrivelse: String? = null,
)

package no.nav.modiapersonoversikt.service.oppgavebehandling

data class Oppgave(
    val oppgaveId: String,
    val fnr: String,
    val henvendelseId: String?,
    val erSTOOppgave: Boolean,
)

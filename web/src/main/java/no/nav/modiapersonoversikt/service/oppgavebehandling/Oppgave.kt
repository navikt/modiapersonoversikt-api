package no.nav.modiapersonoversikt.service.oppgavebehandling

import java.time.LocalDate
import java.time.LocalDateTime

data class Oppgave(
    val oppgaveId: String,
    val fnr: String,
    val henvendelseId: String?,
    val erSTOOppgave: Boolean,
    val tildeltEnhetsnr: String,
    val fristFerdigstillelse: LocalDate? = null,
    val tema: String,
    val temagruppe: String? = null,
    val oppgavetype: String,
    val prioritet: String,
    val status: String,
    val aktivDato: LocalDate,
    val endretAvEnhetsnr: String? = null,
    val opprettetAvEnhetsnr: String? = null,
    val saksreferanse: String? = null,
    val beskrivelse: String? = null,
    val opprettetTidspunkt: LocalDateTime? = null,
)

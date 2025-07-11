package no.nav.modiapersonoversikt.service.oppgavebehandling

import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.oppgave.OppgaveKodeverk
import java.time.LocalDate

data class OpprettOppgaveResponse(
    val id: String,
)

data class OpprettOppgaveRequest(
    val fnr: String,
    val behandlesAvApplikasjon: String,
    val beskrivelse: String,
    val temagruppe: String,
    val tema: String,
    val oppgavetype: String,
    val behandlingstype: String,
    val prioritet: OppgaveKodeverk.Prioritet.PrioritetKode,
    val underkategoriKode: String?,
    val opprettetavenhetsnummer: String,
    val oppgaveFrist: LocalDate,
    val valgtEnhetsId: String,
    val behandlingskjedeId: String,
    val dagerFrist: Int,
    val ansvarligEnhetId: String,
    val ansvarligIdent: String?,
)

data class OpprettSkjermetOppgaveRequest(
    val fnr: String,
    val behandlesAvApplikasjon: String,
    val beskrivelse: String,
    val temagruppe: String,
    val tema: String,
    val oppgavetype: String,
    val behandlingstype: String,
    val prioritet: OppgaveKodeverk.Prioritet.PrioritetKode,
    val underkategoriKode: String?,
    val opprettetavenhetsnummer: String,
    val oppgaveFrist: LocalDate,
)

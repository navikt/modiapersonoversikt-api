package no.nav.sbl.dialogarena.modiabrukerdialog.api.service

import java.time.LocalDate

interface OpprettOppgaveRest {
    fun opprettOppgave(request: SkjermetOppgave)
}

data class SkjermetOppgave(
        val opprettetAvEnhetsnr: String,
        val aktoerId: String,
        val behandlesAvApplikasjon: String,
        val beskrivelse: String,
        val temagruppe: String,
        val tema: String,
        val behandlingstema: String,
        val oppgavetype : String,
        val behandlingstype : String,
        val aktivDato : LocalDate,
        val fristFerdigstillelse : LocalDate,
        val prioritet : String
);
package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.models.*

fun OppgaveJsonDTO.asPatch() = PatchOppgaveRequestJsonDTO(
        id = requireNotNull(this.id) { "Kan ikke konverteres til Patch-object siden `id` er null" },
        versjon = this.versjon,
        aktoerId = this.aktoerId,
        orgnr = this.orgnr,
        endretAvEnhetsnr = this.endretAvEnhetsnr,
        tilordnetRessurs = this.tilordnetRessurs,
        tildeltEnhetsnr = this.tildeltEnhetsnr,
        mappeId = this.mappeId,
        status = convertEnum<OppgaveJsonDTO.Status, PatchOppgaveRequestJsonDTO.Status>(this.status),
        prioritet = convertEnum<OppgaveJsonDTO.Prioritet, PatchOppgaveRequestJsonDTO.Prioritet>(this.prioritet),
        behandlingstema = this.behandlingstema,
        behandlingstype = this.behandlingstype,
        fristFerdigstillelse = this.fristFerdigstillelse,
        aktivDato = this.aktivDato,
        oppgavetype = this.oppgavetype,
        tema = this.tema,
        journalpostId = this.journalpostId,
        saksreferanse = this.saksreferanse
)

fun OppgaveJsonDTO.asPut() = PutOppgaveRequestJsonDTO(
        id = this.id,
        tildeltEnhetsnr = this.tildeltEnhetsnr,
        oppgavetype = this.oppgavetype,
        versjon = this.versjon,
        status = convertEnum(this.status),
        prioritet = convertEnum(this.prioritet),
        aktivDato = this.aktivDato,
        endretAvEnhetsnr = this.endretAvEnhetsnr,
        journalpostId = this.journalpostId,
        journalpostkilde = this.journalpostkilde,
        behandlesAvApplikasjon = this.behandlesAvApplikasjon,
        saksreferanse = this.saksreferanse,
        bnr = this.bnr,
        samhandlernr = this.samhandlernr,
        aktoerId = this.aktoerId,
        orgnr = this.orgnr,
        tilordnetRessurs = this.tilordnetRessurs,
        beskrivelse = this.beskrivelse,
        temagruppe = this.temagruppe,
        tema = this.tema,
        behandlingstema = this.behandlingstema,
        behandlingstype = this.behandlingstype,
        mappeId = this.mappeId,
        metadata = this.metadata,
        fristFerdigstillelse = this.fristFerdigstillelse
)


fun OppgaveJsonDTO.asGetResponse() = GetOppgaveResponseJsonDTO(
        tildeltEnhetsnr = this.tildeltEnhetsnr,
        oppgavetype = this.oppgavetype,
        versjon = this.versjon,
        prioritet = convertEnum(this.prioritet),
        status = convertEnum(this.status),
        aktivDato = this.aktivDato,
        id = this.id,
        endretAvEnhetsnr = this.endretAvEnhetsnr,
        opprettetAvEnhetsnr = this.opprettetAvEnhetsnr,
        journalpostId = this.journalpostId,
        journalpostkilde = this.journalpostkilde,
        behandlesAvApplikasjon = this.behandlesAvApplikasjon,
        saksreferanse = this.saksreferanse,
        bnr = this.bnr,
        samhandlernr = this.samhandlernr,
        aktoerId = this.aktoerId,
        identer = this.identer,
        orgnr = this.orgnr,
        tilordnetRessurs = this.tilordnetRessurs,
        beskrivelse = this.beskrivelse,
        temagruppe = this.temagruppe,
        tema = this.tema,
        behandlingstema = this.behandlingstema,
        behandlingstype = this.behandlingstype,
        mappeId = this.mappeId,
        opprettetAv = this.opprettetAv,
        endretAv = this.endretAv,
        metadata = this.metadata,
        fristFerdigstillelse = this.fristFerdigstillelse,
        opprettetTidspunkt = this.opprettetTidspunkt,
        ferdigstiltTidspunkt = this.ferdigstiltTidspunkt,
        endretTidspunkt = this.endretTidspunkt
)

internal inline fun <S : Enum<S>, reified T : Enum<T>> convertEnum(value: S): T {
    val allowValues: Array<T> = T::class.java.enumConstants
    return allowValues
            .find { it.name == value.name }
            ?: throw IllegalStateException("Fant ikke gyldig enum verdi")
}

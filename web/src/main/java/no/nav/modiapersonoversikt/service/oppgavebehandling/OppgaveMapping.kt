package no.nav.modiapersonoversikt.service.oppgavebehandling

import no.nav.modiapersonoversikt.consumer.oppgave.generated.models.GetOppgaveResponseJsonDTO
import no.nav.modiapersonoversikt.consumer.oppgave.generated.models.OppgaveJsonDTO
import no.nav.modiapersonoversikt.consumer.oppgave.generated.models.PutOppgaveRequestJsonDTO
import no.nav.modiapersonoversikt.consumer.oppgave.generated.models.PutOppgaveResponseJsonDTO

fun GetOppgaveResponseJsonDTO.toOppgaveJsonDTO(): OppgaveJsonDTO = OppgaveJsonDTO(
    tildeltEnhetsnr = this.tildeltEnhetsnr,
    oppgavetype = this.oppgavetype,
    versjon = this.versjon,
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
    endretTidspunkt = this.endretTidspunkt,
    status =
    OppgaveMappingHelpers.convertEnumToJson(this.status),
    prioritet =
    OppgaveMappingHelpers.convertEnumToJson(this.prioritet)
)

fun PutOppgaveResponseJsonDTO.toOppgaveJsonDTO(): OppgaveJsonDTO = OppgaveJsonDTO(
    tildeltEnhetsnr = this.tildeltEnhetsnr,
    oppgavetype = this.oppgavetype,
    versjon = this.versjon,
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
    endretTidspunkt = this.endretTidspunkt,
    status =
    OppgaveMappingHelpers.convertEnumToJson(this.status),
    prioritet =
    OppgaveMappingHelpers.convertEnumToJson(this.prioritet)
)

fun OppgaveJsonDTO.toPutOppgaveRequestJsonDTO(): PutOppgaveRequestJsonDTO =
    PutOppgaveRequestJsonDTO(
        tildeltEnhetsnr = this.tildeltEnhetsnr,
        oppgavetype = this.oppgavetype,
        versjon = this.versjon,
        aktivDato = this.aktivDato,
        id =
        OppgaveMappingHelpers.requiredOppgaveId(this.id),
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
        fristFerdigstillelse = this.fristFerdigstillelse,
        status =
        OppgaveMappingHelpers.convertEnumToPutReq(this.status),
        prioritet =
        OppgaveMappingHelpers.convertEnumToPutReq(this.prioritet)
    )

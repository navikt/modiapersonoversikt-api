package no.nav.modiapersonoversikt.service.oppgavebehandling

import no.nav.modiapersonoversikt.consumer.oppgave.generated.models.*

fun OppgaveJsonDTO.toPostOppgaveResponseJsonDTO(): PostOppgaveResponseJsonDTO =
    PostOppgaveResponseJsonDTO(
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
        OppgaveMappingHelpers.convertEnumToPostResp(this.status),
        prioritet =
        OppgaveMappingHelpers.convertEnumToPostResp(this.prioritet)
    )

fun OppgaveJsonDTO.toGetOppgaveResponseJsonDTO(): GetOppgaveResponseJsonDTO =
    GetOppgaveResponseJsonDTO(
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
        OppgaveMappingHelpers.convertEnumToGetResp(this.status),
        prioritet =
        OppgaveMappingHelpers.convertEnumToGetResp(this.prioritet)
    )

fun OppgaveJsonDTO.toPutOppgaveResponseJsonDTO(): PutOppgaveResponseJsonDTO =
    PutOppgaveResponseJsonDTO(
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
        OppgaveMappingHelpers.convertEnumToPutResp(this.status),
        prioritet =
        OppgaveMappingHelpers.convertEnumToPutResp(this.prioritet)
    )

fun PutOppgaveRequestJsonDTO.toPutOppgaveResponseJsonDTO(): PutOppgaveResponseJsonDTO =
    PutOppgaveResponseJsonDTO(
        tildeltEnhetsnr = this.tildeltEnhetsnr,
        oppgavetype = this.oppgavetype,
        versjon = this.versjon,
        aktivDato = this.aktivDato,
        id = this.id,
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
        OppgaveMappingHelpers.convertEnumToPutResp(this.status),
        prioritet =
        OppgaveMappingHelpers.convertEnumToPutResp(this.prioritet)
    )

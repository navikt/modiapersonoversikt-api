package no.nav.modiapersonoversikt.legacy.api.domain.oppgave

import no.nav.modiapersonoversikt.legacy.api.domain.oppgave.generated.models.*

public fun OppgaveJsonDTO.toPostOppgaveResponseJsonDTO(): PostOppgaveResponseJsonDTO =
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
        no.nav.modiapersonoversikt.legacy.api.domain.oppgave.OppgaveMappingHelpers.convertEnumToPostResp(this.status),
        prioritet =
        no.nav.modiapersonoversikt.legacy.api.domain.oppgave.OppgaveMappingHelpers.convertEnumToPostResp(this.prioritet)
    )

public fun OppgaveJsonDTO.toGetOppgaveResponseJsonDTO(): GetOppgaveResponseJsonDTO =
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
        no.nav.modiapersonoversikt.legacy.api.domain.oppgave.OppgaveMappingHelpers.convertEnumToGetResp(this.status),
        prioritet =
        no.nav.modiapersonoversikt.legacy.api.domain.oppgave.OppgaveMappingHelpers.convertEnumToGetResp(this.prioritet)
    )

public fun OppgaveJsonDTO.toPutOppgaveResponseJsonDTO(): PutOppgaveResponseJsonDTO =
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
        no.nav.modiapersonoversikt.legacy.api.domain.oppgave.OppgaveMappingHelpers.convertEnumToPutResp(this.status),
        prioritet =
        no.nav.modiapersonoversikt.legacy.api.domain.oppgave.OppgaveMappingHelpers.convertEnumToPutResp(this.prioritet)
    )

public fun PutOppgaveRequestJsonDTO.toPutOppgaveResponseJsonDTO(): PutOppgaveResponseJsonDTO =
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
        no.nav.modiapersonoversikt.legacy.api.domain.oppgave.OppgaveMappingHelpers.convertEnumToPutResp(this.status),
        prioritet =
        no.nav.modiapersonoversikt.legacy.api.domain.oppgave.OppgaveMappingHelpers.convertEnumToPutResp(this.prioritet)
    )

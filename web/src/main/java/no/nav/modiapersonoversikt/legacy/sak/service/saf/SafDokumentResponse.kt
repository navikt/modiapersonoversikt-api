package no.nav.modiapersonoversikt.legacy.sak.service.saf

import java.time.LocalDateTime

data class SafDokumentResponse(val data: Data?, val errors: List<SafError>?)

data class Data(val dokumentoversiktBruker: DokumentoversiktBruker?)

data class DokumentoversiktBruker(val journalposter: List<Journalpost>?)

data class Journalpost(
    val avsenderMottaker: AvsenderMottaker?,
    val bruker: Bruker?,
    val dokumenter: List<DokumentInfo>?,
    val datoOpprettet: LocalDateTime,
    val journalpostId: String,
    val journalposttype: String?,
    val journalstatus: String?,
    val relevanteDatoer: List<RelevantDato>?,
    val sak: Sak?,
    val tema: String?,
    val temanavn: String?,
    val tittel: String?
)

data class AvsenderMottaker(
    val erLikBruker: Boolean,
    val navn: String?
)

data class Bruker(
    val id: String?,
    val type: String?
)

data class DokumentInfo(
    val dokumentInfoId: String,
    val dokumentvarianter: List<Dokumentvariant>,
    val logiskeVedlegg: List<LogiskVedlegg>,
    val tittel: String?
)

data class Dokumentvariant(
    var saksbehandlerHarTilgang: Boolean?,
    var variantformat: String?,
    var skjerming: String?
)

data class LogiskVedlegg(
    val tittel: String?
)

data class RelevantDato(
    val dato: LocalDateTime,
    val datotype: String
)

data class Sak(
    val arkivsaksnummer: String?,
    val arkivsaksystem: String?,
    val fagsakId: String?,
    val fagsaksystem: String?
)

data class SafError(
    val message: String?,
    val locations: List<Location>?
)

data class Location(
    val line: Number?,
    val column: Number?
)

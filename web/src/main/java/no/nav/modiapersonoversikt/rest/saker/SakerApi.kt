package no.nav.modiapersonoversikt.rest.saker

import no.nav.modiapersonoversikt.commondomain.sak.Baksystem
import no.nav.modiapersonoversikt.commondomain.sak.Entitet
import no.nav.modiapersonoversikt.commondomain.sak.Feilmelding
import no.nav.modiapersonoversikt.service.saf.domain.Dokument.DokumentStatus
import no.nav.modiapersonoversikt.service.saf.domain.Kommunikasjonsretning
import no.nav.modiapersonoversikt.service.sakstema.domain.BehandlingsStatus
import java.time.LocalDateTime

object SakerApi {
    data class Resultat(
        val resultat: List<Sakstema>,
    )

    data class Sakstema(
        val temakode: String,
        val temanavn: String,
        val erGruppert: Boolean,
        val behandlingskjeder: List<Behandlingskjede>,
        val dokumentMetadata: List<Dokumentmetadata>,
        val tilhorendeSaker: List<Sak>,
        val feilkoder: List<Int>,
        val harTilgang: Boolean,
    )

    data class Behandlingskjede(
        val status: BehandlingsStatus,
        val sistOppdatert: LocalDateTime,
    )

    data class Dokumentmetadata(
        val id: String,
        val retning: Kommunikasjonsretning,
        val dato: LocalDateTime,
        val lestDato: LocalDateTime?,
        val navn: String,
        val journalpostId: String,
        val hoveddokument: Dokument,
        val vedlegg: List<Dokument>,
        val avsender: Entitet,
        val mottaker: Entitet,
        val tilhorendeSaksid: String,
        val tilhorendeFagsaksid: String?,
        val baksystem: Set<Baksystem>,
        val temakode: String,
        val temakodeVisning: String,
        val ettersending: Boolean,
        val erJournalfort: Boolean,
        val feil: Feil,
    )

    data class Dokument(
        val tittel: String,
        val dokumentreferanse: String?,
        val kanVises: Boolean,
        val logiskDokument: Boolean,
        val skjerming: String?,
        val erKassert: Boolean,
        val dokumentStatus: DokumentStatus?,
    )

    data class Feil(
        val inneholderFeil: Boolean,
        val feilmelding: Feilmelding?,
    )

    data class Sak(
        val temakode: String,
        val saksid: String,
        val fagsaksnummer: String?,
        val avsluttet: LocalDateTime?,
        val fagsystem: String,
        val baksystem: Baksystem,
    )
}

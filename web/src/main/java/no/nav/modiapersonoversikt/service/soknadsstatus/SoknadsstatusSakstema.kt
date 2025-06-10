package no.nav.modiapersonoversikt.service.soknadsstatus

import no.nav.modiapersonoversikt.service.saf.domain.DokumentMetadata
import no.nav.modiapersonoversikt.service.sakstema.domain.Sak

data class SoknadsstatusSakstema(
    val temakode: String,
    val temanavn: String,
    val erGruppert: Boolean,
    val soknadsstatus: Soknadsstatus = Soknadsstatus(),
    val dokumentMetadata: List<DokumentMetadata> = emptyList(),
    val tilhorendeSaker: List<Sak> = emptyList(),
    val feilkoder: List<Int> = emptyList(),
)

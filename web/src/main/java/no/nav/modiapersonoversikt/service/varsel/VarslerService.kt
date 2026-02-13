package no.nav.modiapersonoversikt.service.varsel

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.brukernotifikasjon.Brukernotifikasjon
import java.time.ZonedDateTime

interface VarslerService {
    fun hentAlleVarsler(fnr: Fnr): Result

    interface UnifiedVarsel

    data class Result(
        val feil: List<String>,
        val varsler: List<Varsel>,
    )

    data class Varsel(
        val type: String,
        val varselId: String,
        val aktiv: Boolean,
        val produsent: String,
        val sensitivitet: String,
        val innhold: Brukernotifikasjon.Innhold,
        val eksternVarsling: VarselInfo,
        val opprettet: ZonedDateTime,
    )

    data class VarselInfo(
        val sendt: Boolean,
        val sendtTidspunkt: ZonedDateTime,
        val renotifikasjonSendt: Boolean,
        val renotifikasjonTidspunkt: ZonedDateTime,
        val sendteKanaler: List<String>,
        val feilhistorikk: List<Brukernotifikasjon.Feilhistorikk>,
        val sistOppdatert: ZonedDateTime
    )
}

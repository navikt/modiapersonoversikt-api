package no.nav.modiapersonoversikt.service.varsel

import no.nav.common.types.identer.Fnr

interface VarslerService {
    fun hentAlleVarsler(fnr: Fnr): Result

    interface UnifiedVarsel

    data class Result(val feil: List<String>, val varsler: List<UnifiedVarsel>)
}

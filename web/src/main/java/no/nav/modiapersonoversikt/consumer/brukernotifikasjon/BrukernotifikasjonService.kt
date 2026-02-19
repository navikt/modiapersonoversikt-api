package no.nav.modiapersonoversikt.consumer.brukernotifikasjon

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.service.varsel.VarslerService

class BrukernotifikasjonService(
    private val client: Brukernotifikasjon.Client,
) : Brukernotifikasjon.Service {
    override fun hentAlleBrukernotifikasjoner(fnr: Fnr): List<VarslerService.Varsel> =
        client
            .hentAlleBrukernotifikasjoner(fnr)
            .map { Brukernotifikasjon.Mapper.lagVarselFraEvent(it) }
            .sortedByDescending { it.eksternVarsling.sistOppdatert }
}

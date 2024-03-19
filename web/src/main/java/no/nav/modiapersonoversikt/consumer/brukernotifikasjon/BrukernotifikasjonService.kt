package no.nav.modiapersonoversikt.consumer.brukernotifikasjon

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.brukernotifikasjon.Brukernotifikasjon.Type

class BrukernotifikasjonService(
    private val client: Brukernotifikasjon.Client,
) : Brukernotifikasjon.Service {
    override fun hentAlleBrukernotifikasjoner(fnr: Fnr): List<Brukernotifikasjon.Event> {
        return client.hentAlleBrukernotifikasjoner(fnr)
            .filter { producerDenyList.contains(it.produsent.appnavn).not() }
            .map {
                Brukernotifikasjon.Mapper.byggVarslingsTidspunktV2(fnr.get(), it)
            }
    }

    override fun hentBrukernotifikasjoner(
        type: Type,
        fnr: Fnr,
    ): List<Brukernotifikasjon.Event> {
        return client.hentBrukernotifikasjoner(type, fnr)
            .filter { producerDenyList.contains(it.produsent).not() }
            .map {
                Brukernotifikasjon.Mapper.byggVarslingsTidspunkt(it)
            }
    }

    companion object {
        val producerDenyList = listOf("sosialhjelp-dialog-api", "sosialhjelp-dialog-api-dev")
    }
}

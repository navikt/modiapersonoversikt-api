package no.nav.modiapersonoversikt.consumer.brukernotifikasjon

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.brukernotifikasjon.Brukernotifikasjon.Type
import no.nav.modiapersonoversikt.utils.ConcurrencyUtils.runInParallel

class BrukernotifikasjonService(
    private val client: Brukernotifikasjon.Client
) : Brukernotifikasjon.Service {
    override fun hentAlleBrukernotifikasjoner(fnr: Fnr): List<Brukernotifikasjon.Event> {
        return listOf(
            { hentBrukernotifikasjoner(Type.BESKJED, fnr) },
            { hentBrukernotifikasjoner(Type.INNBOKS, fnr) },
            { hentBrukernotifikasjoner(Type.OPPGAVE, fnr) },
        )
            .runInParallel()
            .flatten()
    }

    override fun hentBrukernotifikasjoner(type: Type, fnr: Fnr): List<Brukernotifikasjon.Event> {
        return client.hentBrukernotifikasjoner(type, fnr)
            .filter { producerDenyList.contains(it.produsent).not() }
    }

    companion object {
        val producerDenyList = listOf("srvsosial-dialog")
    }
}

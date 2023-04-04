package no.nav.modiapersonoversikt.consumer.brukernotifikasjon

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.brukernotifikasjon.Brukernotifikasjon.Type
import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.modiapersonoversikt.utils.ConcurrencyUtils.makeThreadSwappable
import no.nav.personoversikt.common.utils.ConcurrencyUtils.runInParallel

class BrukernotifikasjonService(
    private val client: Brukernotifikasjon.Client,
    private val unleashService: UnleashService
) : Brukernotifikasjon.Service {
    override fun hentAlleBrukernotifikasjoner(fnr: Fnr): List<Brukernotifikasjon.Event> {
        return listOf(
            { hentBrukernotifikasjoner(Type.BESKJED, fnr) },
            { hentBrukernotifikasjoner(Type.INNBOKS, fnr) },
            { hentBrukernotifikasjoner(Type.OPPGAVE, fnr) },
        )
            .map { makeThreadSwappable(it) }
            .runInParallel()
            .flatten()
    }

    override fun hentBrukernotifikasjoner(type: Type, fnr: Fnr): List<Brukernotifikasjon.Event> {
        return client.hentBrukernotifikasjoner(type, fnr)
            .filter { producerDenyList.contains(it.produsent).not() }
            .map {
                if (unleashService.isEnabled(Feature.VIS_REVARSLING)) {
                    Brukernotifikasjon.Mapper.byggVarslingsTidspunkt(it)
                } else {
                    it
                }
            }
    }

    companion object {
        val producerDenyList = listOf("sosialhjelp-dialog-api", "sosialhjelp-dialog-api-dev")
    }
}

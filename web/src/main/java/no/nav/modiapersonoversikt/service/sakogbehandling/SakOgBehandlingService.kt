package no.nav.modiapersonoversikt.service.sakogbehandling

import no.nav.modiapersonoversikt.service.sakstema.domain.Behandlingskjede
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak

interface SakOgBehandlingService {
    fun hentAlleSaker(fnr: String): List<Sak>

    fun hentBehandlingskjederGruppertPaaTema(fnr: String): Map<String, List<Behandlingskjede>>
}

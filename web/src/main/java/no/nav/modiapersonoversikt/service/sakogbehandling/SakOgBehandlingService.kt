package no.nav.modiapersonoversikt.service.sakogbehandling

import no.nav.modiapersonoversikt.service.sakstema.domain.Behandlingskjede
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable

@CacheConfig(cacheNames = ["endpointCache"], keyGenerator = "userkeygenerator")
interface SakOgBehandlingService {
    @Cacheable
    fun hentAlleSaker(fnr: String): List<Sak>

    @Cacheable
    fun hentBehandlingskjederGruppertPaaTema(fnr: String): Map<String, List<Behandlingskjede?>>
}

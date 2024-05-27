package no.nav.modiapersonoversikt.service.varsel

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.brukernotifikasjon.Brukernotifikasjon
import no.nav.personoversikt.common.logging.TjenestekallLogg
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable

@CacheConfig(cacheNames = ["varslingCache"], keyGenerator = "userkeygenerator")
open class VarslerServiceImpl(
    private val brukernotifikasjonService: Brukernotifikasjon.Service,
) : VarslerService {
    private val log = LoggerFactory.getLogger("VarslerService")

    @Cacheable
    override fun hentAlleVarsler(fnr: Fnr): VarslerService.Result {
        return try {
            val varsler = brukernotifikasjonService.hentAlleBrukernotifikasjoner(fnr)
            VarslerService.Result(
                feil = listOf(),
                varsler = varsler,
            )
        } catch (e: Throwable) {
            TjenestekallLogg.error("Feilet ved uthentig av varsler", fields = mapOf(), throwable = e)
            VarslerService.Result(
                feil = listOf("Feil ved uthenting av notifikasjoner"),
                varsler = listOf(),
            )
        }
    }
}

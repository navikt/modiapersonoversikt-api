package no.nav.modiapersonoversikt.service.sakogbehandling

import no.nav.modiapersonoversikt.commondomain.sak.Baksystem
import no.nav.modiapersonoversikt.commondomain.sak.FeilendeBaksystemException
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.service.sakstema.domain.Behandlingskjede
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.binding.SakOgBehandlingV1
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable

@CacheConfig(cacheNames = ["endpointCache"], keyGenerator = "userkeygenerator")
open class SakOgBehandlingServiceImpl(
    private val sakOgBehandlingPortType: SakOgBehandlingV1,
    private val pdlOppslagService: PdlOppslagService
) : SakOgBehandlingService {
    @Cacheable
    override fun hentAlleSaker(fnr: String): List<Sak> {
        return try {
            val aktorId = pdlOppslagService.hentAktorId(fnr)
            val request = FinnSakOgBehandlingskjedeListeRequest()
            request.aktoerREF = aktorId
            val sobSaker = sakOgBehandlingPortType.finnSakOgBehandlingskjedeListe(request).sak
            SakOgBehandlingFilter.filtrerSaker(sobSaker)
        } catch (ex: RuntimeException) {
            logger.error("Det skjedde en uventet feil mot Sak og Behandling", ex)
            throw FeilendeBaksystemException(
                Baksystem.SAK_OG_BEHANDLING
            )
        }
    }

    @Cacheable
    override fun hentBehandlingskjederGruppertPaaTema(fnr: String): Map<String, List<Behandlingskjede>> {
        return hentAlleSaker(fnr)
            .associate { sak ->
                val tema = sak.sakstema.value
                val behandlingskjeder = tilBehandligskjeder(sak)
                tema to behandlingskjeder
            }
    }

    private fun tilBehandligskjeder(sak: Sak): List<Behandlingskjede> {
        return SakOgBehandlingFilter.filtrerBehandlinger(sak.behandlingskjede)
            .map {
                Behandlingskjede()
                    .withStatus(SakOgBehandlingFilter.behandlingsstatus(it))
                    .withSistOppdatert(SakOgBehandlingFilter.behandlingsdato(it).atStartOfDay())
            }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(SakOgBehandlingServiceImpl::class.java)
    }
}

package no.nav.modiapersonoversikt.service.soknadsstatus

import no.nav.modiapersonoversikt.consumer.modiaSoknadsstatusApi.generated.apis.SoknadsstatusControllerApi
import no.nav.modiapersonoversikt.consumer.modiaSoknadsstatusApi.generated.models.Behandling
import no.nav.modiapersonoversikt.consumer.modiaSoknadsstatusApi.generated.models.Hendelse
import no.nav.modiapersonoversikt.utils.BoundedOnBehalfOfTokenClient
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable

interface SoknadsstatusService {
    fun hentHendelser(ident: String): List<Hendelse>
    fun hentBehandlinger(ident: String): List<Behandling>
    fun hentBehandlingerMedHendelser(ident: String, filtrer: Boolean = true): List<Behandling>
    fun hentBehandlingerGruppertPaaTema(ident: String, filtrer: Boolean = true): Map<String, Soknadsstatus>
    fun grupperBehandlingerPaaTema(behandlinger: List<Behandling>, filtrer: Boolean = true): Map<String, Soknadsstatus>
    fun ping()
}

@CacheConfig(cacheNames = ["soknadsstatusCache"], keyGenerator = "userkeygenerator")
open class SoknadsstatusServiceImpl(
    private val oboTokenClient: BoundedOnBehalfOfTokenClient,
    private val soknadsstatusApi: SoknadsstatusControllerApi = SoknadsstatusApiFactory.createSoknadsstatusApi(
        oboTokenClient
    )
) : SoknadsstatusService {

    @Cacheable
    override fun hentHendelser(ident: String): List<Hendelse> {
        return soknadsstatusApi.hentAlleHendelser(ident)
    }

    @Cacheable
    override fun hentBehandlinger(ident: String): List<Behandling> {
        return soknadsstatusApi.hentAlleBehandlinger(ident)
    }

    @Cacheable
    override fun hentBehandlingerMedHendelser(ident: String, filtrer: Boolean): List<Behandling> {
        val behandlinger = soknadsstatusApi.hentAlleBehandlinger(ident, inkluderHendelser = true)
        return if (filtrer) Filter.filtrerOgSorterBehandligner(behandlinger) else behandlinger
    }

    @Cacheable
    override fun hentBehandlingerGruppertPaaTema(ident: String, filtrer: Boolean): Map<String, Soknadsstatus> {
        val behandlinger = hentBehandlingerMedHendelser(ident, filtrer)
        return grupperBehandlingerPaaTema(behandlinger)
    }

    override fun grupperBehandlingerPaaTema(
        behandlinger: List<Behandling>,
        filtrer: Boolean
    ): Map<String, Soknadsstatus> {
        var tmpBehandlinger = behandlinger
        if (filtrer) {
            tmpBehandlinger = Filter.filtrerOgSorterBehandligner(behandlinger)
        }
        val temamap = mutableMapOf<String, Soknadsstatus>()
        for (behandling in tmpBehandlinger) {
            val temastatus = temamap[behandling.behandlingsTema] ?: Soknadsstatus()
            when (behandling.status) {
                Behandling.Status.UNDER_BEHANDLING -> temastatus.underBehandling++
                Behandling.Status.FERDIG_BEHANDLET -> temastatus.ferdigBehandlet++
                Behandling.Status.AVBRUTT -> temastatus.avbrutt++
            }
            temamap[behandling.behandlingsTema] = temastatus
        }
        return temamap
    }

    override fun ping() {
        soknadsstatusApi.ping()
    }
}

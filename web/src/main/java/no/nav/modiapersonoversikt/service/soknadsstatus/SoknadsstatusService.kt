package no.nav.modiapersonoversikt.service.soknadsstatus

import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import no.nav.modiapersonoversikt.consumer.modiaSoknadsstatusApi.generated.apis.SoknadsstatusControllerApi
import no.nav.modiapersonoversikt.consumer.modiaSoknadsstatusApi.generated.models.Behandling
import no.nav.modiapersonoversikt.consumer.modiaSoknadsstatusApi.generated.models.FnrRequest
import no.nav.modiapersonoversikt.consumer.modiaSoknadsstatusApi.generated.models.Hendelse
import no.nav.modiapersonoversikt.utils.BoundedOnBehalfOfTokenClient
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable

interface SoknadsstatusService {
    fun hentHendelser(ident: String): List<Hendelse>

    fun hentBehandlinger(ident: String): List<Behandling>

    fun hentBehandlingerMedHendelser(ident: String): List<Behandling>

    fun hentBehandlingerGruppertPaaTema(
        ident: String,
        behandlingerSomAlleredeErInkludert: Set<String> = setOf(),
    ): Map<String, Soknadsstatus>

    fun ping()
}

@CacheConfig(cacheNames = ["soknadsstatusCache"], keyGenerator = "userkeygenerator")
open class SoknadsstatusServiceImpl(
    private val oboTokenClient: BoundedOnBehalfOfTokenClient,
    private val soknadsstatusApi: SoknadsstatusControllerApi =
        SoknadsstatusApiFactory.createSoknadsstatusApi(
            oboTokenClient,
        ),
) : SoknadsstatusService {
    @Cacheable
    override fun hentHendelser(ident: String): List<Hendelse> {
        return soknadsstatusApi.hentAlleHendelser(FnrRequest(ident)).orEmpty()
    }

    @Cacheable
    override fun hentBehandlinger(ident: String): List<Behandling> {
        return soknadsstatusApi.hentAlleBehandlinger(FnrRequest(ident)).orEmpty()
    }

    @Cacheable
    override fun hentBehandlingerMedHendelser(ident: String): List<Behandling> {
        val behandlinger = soknadsstatusApi.hentAlleBehandlinger(FnrRequest(ident), inkluderHendelser = true)
        return behandlinger?.let { Filter.filtrerOgSorterBehandligner(it) }.orEmpty()
    }

    @Cacheable
    override fun hentBehandlingerGruppertPaaTema(
        ident: String,
        behandlingerSomAlleredeErInkludert: Set<String>,
    ): Map<String, Soknadsstatus> {
        val behandlinger = hentBehandlingerMedHendelser(ident)
        return grupperBehandlingerPaaTema(behandlinger, behandlingerSomAlleredeErInkludert)
    }

    private fun grupperBehandlingerPaaTema(
        behandlinger: List<Behandling>,
        behandlingerSomAlleredeErInkludert: Set<String>,
    ): Map<String, Soknadsstatus> {
        val temamap = mutableMapOf<String, Soknadsstatus>()
        for (behandling in behandlinger) {
            if (behandlingerSomAlleredeErInkludert.contains(behandling.behandlingId)) continue
            val temastatus = temamap[behandling.sakstema] ?: Soknadsstatus()
            when (behandling.status) {
                Behandling.Status.UNDER_BEHANDLING -> temastatus.underBehandling++
                Behandling.Status.FERDIG_BEHANDLET -> temastatus.ferdigBehandlet++
                Behandling.Status.AVBRUTT -> temastatus.avbrutt++
            }
            if (temastatus.sistOppdatert == null || behandling.sistOppdatert.isAfter(temastatus.sistOppdatert!!.toJavaLocalDateTime())) {
                temastatus.sistOppdatert = behandling.sistOppdatert.toKotlinLocalDateTime()
            }

            temamap[behandling.sakstema] = temastatus
        }
        return temamap
    }

    override fun ping() {
        soknadsstatusApi.ping()
    }
}

package no.nav.modiapersonoversikt.consumer.veilarboppfolging

import com.github.benmanes.caffeine.cache.Cache
import no.nav.common.health.HealthCheckUtils
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.types.identer.Fnr
import no.nav.common.utils.UrlUtils
import no.nav.modiapersonoversikt.domain.veilarbvedtaksstotte.api.generated.apis.KodeverkFor14AVedtakApi
import no.nav.modiapersonoversikt.domain.veilarbvedtaksstotte.api.generated.apis.Siste14AVedtakV2Api
import no.nav.modiapersonoversikt.domain.veilarbvedtaksstotte.api.generated.models.*
import no.nav.modiapersonoversikt.infrastructure.cache.CacheUtils
import no.nav.modiapersonoversikt.infrastructure.ping.Pingable
import java.time.Duration
import java.time.LocalDateTime

interface VeilarbvedtaksstotteService : Pingable {
    fun hentSiste14aVedtak(fodselsnummer: Fnr): Siste14aVedtak?
}

data class Siste14aVedtak(
    val innsatsgruppe: InnsatsgruppeDetaljert,
    val hovedmal: HovedmalDetaljert?,
    val fattetDato: LocalDateTime,
    val fraArena: Boolean?,
)

class VeilarbvedtaksstotteServiceImpl(
    private val siste14AVedtakV2Api: Siste14AVedtakV2Api,
    private val kodeverkFor14AVedtakApi: KodeverkFor14AVedtakApi,
    private val cache: Cache<Fnr, Siste14aVedtak> = CacheUtils.createCache(),
    private val innsatsgruppeCache: Cache<String, InnsatsgruppeDetaljert> = CacheUtils.createCache(expireAfterWrite = Duration.ofHours(24)),
    private val hovedmalCache: Cache<String, HovedmalDetaljert> = CacheUtils.createCache(expireAfterWrite = Duration.ofHours(24)),
) : VeilarbvedtaksstotteService {
    override fun hentSiste14aVedtak(fnr: Fnr): Siste14aVedtak? =
        cache.get(fnr) {
            siste14AVedtakV2Api.hentSiste14aVedtak(Siste14aVedtakRequest(fnr.get()))?.let { mapToSiste14aVedtak(it) }
        }

    override fun ping() =
        SelfTestCheck(
            "VeilarbvedtaksstotteApi via ${siste14AVedtakV2Api.client.baseUrl}",
            false,
        ) {
            HealthCheckUtils.pingUrl(
                UrlUtils.joinPaths(siste14AVedtakV2Api.client.baseUrl, "/internal/health/liveness"),
                siste14AVedtakV2Api.client.client,
            )
        }

    private fun mapToSiste14aVedtak(dto: Siste14aVedtakDTO) =
        Siste14aVedtak(
            innsatsgruppe = innsatsgruppeCache.get(dto.innsatsgruppe.value) { getInnsatsgruppe(dto.innsatsgruppe.value) },
            hovedmal = dto.hovedmal?.let { hovedmalCache.get(it.value) { getHovedmal(dto.innsatsgruppe.value) } },
            fattetDato = dto.fattetDato.toLocalDateTime(),
            fraArena = dto.fraArena,
        )

    private fun getInnsatsgruppe(innsatsgrupper: String): InnsatsgruppeDetaljert? {
        prepopulerCache()
        return innsatsgruppeCache.getIfPresent(innsatsgrupper)
    }

    private fun getHovedmal(hovedmal: String): HovedmalDetaljert? {
        prepopulerCache()
        return hovedmalCache.getIfPresent(hovedmal)
    }

    private fun prepopulerCache() {
        kodeverkFor14AVedtakApi.henteKodeverk()?.let { kodeverk ->
            kodeverk.innsatsgrupper?.let { innsatsgruppe -> innsatsgruppeCache.putAll(innsatsgruppe.associateBy { it.kode }) }
            kodeverk.hovedmal?.let { hovedmal -> hovedmalCache.putAll(hovedmal.associateBy { it.kode }) }
        }
    }
}

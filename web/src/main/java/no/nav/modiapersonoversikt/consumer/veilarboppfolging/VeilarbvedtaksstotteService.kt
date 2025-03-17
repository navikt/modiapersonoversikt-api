package no.nav.modiapersonoversikt.consumer.veilarboppfolging

import com.github.benmanes.caffeine.cache.Cache
import no.nav.common.health.HealthCheckUtils
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.types.identer.Fnr
import no.nav.common.utils.UrlUtils
import no.nav.modiapersonoversikt.domain.veilarbvedtaksstotte.api.generated.apis.Gjeldende14AVedtakApi
import no.nav.modiapersonoversikt.domain.veilarbvedtaksstotte.api.generated.apis.KodeverkFor14AVedtakApi
import no.nav.modiapersonoversikt.domain.veilarbvedtaksstotte.api.generated.models.*
import no.nav.modiapersonoversikt.infrastructure.cache.CacheUtils
import no.nav.modiapersonoversikt.infrastructure.ping.Pingable
import java.time.Duration
import java.time.LocalDateTime

interface VeilarbvedtaksstotteService : Pingable {
    fun hentGjeldende14aVedtak(fodselsnummer: Fnr): Gjeldende14aVedtak?
}

data class Gjeldende14aVedtakResponse(
    val gjeldende14aVedtak: Gjeldende14aVedtak?,
)

data class Gjeldende14aVedtak(
    val innsatsgruppe: Innsatsgruppe,
    val hovedmal: Hovedmal?,
    val fattetDato: LocalDateTime,
)

data class Innsatsgruppe(
    val kode: String,
    val beskrivelse: String,
)

data class Hovedmal(
    val kode: String,
    val beskrivelse: String,
)

class VeilarbvedtaksstotteServiceImpl(
    private val gjeldende14AVedtakApi: Gjeldende14AVedtakApi,
    private val kodeverkFor14AVedtakApi: KodeverkFor14AVedtakApi,
    private val cache: Cache<Fnr, Gjeldende14aVedtak> = CacheUtils.createCache(),
    private val innsatsgruppeCache: Cache<String, Innsatsgruppe> = CacheUtils.createCache(expireAfterWrite = Duration.ofHours(24)),
    private val hovedmalCache: Cache<String, Hovedmal> = CacheUtils.createCache(expireAfterWrite = Duration.ofHours(24)),
) : VeilarbvedtaksstotteService {
    init {
        prepopulerCache()
    }

    override fun hentGjeldende14aVedtak(fnr: Fnr): Gjeldende14aVedtak? =
        cache.get(fnr) {
            gjeldende14AVedtakApi.hentGjeldende14aVedtakEksternt(Gjeldende14aVedtakRequest(fnr.get()))?.let { mapToGjeldende14aVedtak(it) }
        }

    override fun ping() =
        SelfTestCheck(
            "VeilarbvedtaksstotteApi via ${kodeverkFor14AVedtakApi.client.baseUrl}",
            false,
        ) {
            HealthCheckUtils.pingUrl(
                UrlUtils.joinPaths(kodeverkFor14AVedtakApi.client.baseUrl, "/internal/health/liveness"),
                kodeverkFor14AVedtakApi.client.client,
            )
        }

    private fun mapToGjeldende14aVedtak(dto: Gjeldende14aVedtakDto) =
        Gjeldende14aVedtak(
            innsatsgruppe = innsatsgruppeCache.get(dto.innsatsgruppe.value) { getInnsatsgruppe(dto.innsatsgruppe.value) },
            hovedmal = dto.hovedmal?.let { hovedmal -> hovedmalCache.get(hovedmal.value) { getHovedmal(hovedmal.value) } },
            fattetDato = dto.fattetDato.toLocalDateTime(),
        )

    private fun getInnsatsgruppe(key: String): Innsatsgruppe? {
        kodeverkFor14AVedtakApi.getInnsatsgrupper()?.map {
            innsatsgruppeCache.put(
                it.kode.value,
                Innsatsgruppe(it.kode.value, it.beskrivelse),
            )
        }
        return innsatsgruppeCache.getIfPresent(key)
    }

    private fun getHovedmal(key: String): Hovedmal? {
        kodeverkFor14AVedtakApi.getHovedmal()?.map { hovedmalCache.put(it.kode.value, Hovedmal(it.kode.value, it.beskrivelse)) }
        return hovedmalCache.getIfPresent(key)
    }

    private fun prepopulerCache() {
        kodeverkFor14AVedtakApi.getInnsatsgruppeOgHovedmalKodeverk()?.let { kodeverk ->
            kodeverk.innsatsgrupper.map { innsatsgruppeCache.put(it.kode.value, Innsatsgruppe(it.kode.value, it.beskrivelse)) }
            kodeverk.hovedmal.map { hovedmalCache.put(it.kode.value, Hovedmal(it.kode.value, it.beskrivelse)) }
        }
    }
}

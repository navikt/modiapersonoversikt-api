package no.nav.modiapersonoversikt.consumer.veilarboppfolging

import com.github.benmanes.caffeine.cache.Cache
import no.nav.common.health.HealthCheckUtils
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.types.identer.Fnr
import no.nav.common.utils.UrlUtils
import no.nav.modiapersonoversikt.infrastructure.cache.CacheUtils
import no.nav.modiapersonoversikt.infrastructure.ping.Pingable
import no.nav.modiapersonoversikt.domain.veilarbvedtaksstotte.api.generated.apis.Siste14AVedtakV2Api
import no.nav.modiapersonoversikt.domain.veilarbvedtaksstotte.api.generated.models.Siste14aVedtakDTO
import no.nav.modiapersonoversikt.domain.veilarbvedtaksstotte.api.generated.models.Siste14aVedtakRequest
import java.time.LocalDateTime

interface VeilarbvedtaksstotteService : Pingable {
    fun hentSiste14aVedtak(fodselsnummer: Fnr): Siste14aVedtak?
}

data class Siste14aVedtak(
    val innsatsgruppe: String,
    val hovedmal: String?,
    val fattetDato: LocalDateTime,
    val fraArena: Boolean?,
)

class VeilarbvedtaksstotteServiceImpl(
    private val siste14AVedtakV2Api: Siste14AVedtakV2Api,
    private val cache: Cache<Fnr, Siste14aVedtak> = CacheUtils.createCache(),
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
            HealthCheckUtils.pingUrl(UrlUtils.joinPaths(siste14AVedtakV2Api.client.baseUrl, "/internal/health/liveness"), siste14AVedtakV2Api.client.client)
        }

    private fun mapToSiste14aVedtak(dto: Siste14aVedtakDTO) =
        Siste14aVedtak(
            innsatsgruppe = dto.innsatsgruppe.value,
            hovedmal = dto.hovedmal?.value,
            fattetDato = dto.fattetDato.toLocalDateTime(),
            fraArena = dto.fraArena
        )

}

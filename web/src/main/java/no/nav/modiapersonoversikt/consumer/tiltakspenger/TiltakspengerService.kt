package no.nav.modiapersonoversikt.consumer.tiltakspenger

import no.nav.common.health.HealthCheckUtils
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.types.identer.Fnr
import no.nav.common.utils.UrlUtils
import no.nav.modiapersonoversikt.consumer.tiltakspenger.generated.apis.TiltakspengerApi
import no.nav.modiapersonoversikt.consumer.tiltakspenger.generated.models.VedtakPerioderResponseInner
import no.nav.modiapersonoversikt.consumer.tiltakspenger.generated.models.VedtakReqDTO
import no.nav.modiapersonoversikt.infrastructure.ping.Pingable
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable

interface TiltakspengerService {
    fun hentVedtakPerioder(
        fodselsnummer: Fnr,
        fom: String?,
        tom: String?,
    ): List<VedtakPerioderResponseInner>
}

@CacheConfig(cacheNames = ["tiltakspengerCache"], keyGenerator = "userkeygenerator")
open class TiltakspengerServiceImpl(
    private val tiltakspengerApi: TiltakspengerApi,
) : TiltakspengerService,
    Pingable {
    @Cacheable
    override fun hentVedtakPerioder(
        fodselsnummer: Fnr,
        fom: String?,
        tom: String?,
    ): List<VedtakPerioderResponseInner> =
        tiltakspengerApi.vedtakPerioderPost(vedtakReqDTO = VedtakReqDTO(ident = fodselsnummer.get(), fom, tom)).orEmpty()

    override fun ping() =
        SelfTestCheck("Tiltakspenger datadeling", true) {
            HealthCheckUtils.pingUrl(
                UrlUtils.joinPaths(tiltakspengerApi.client.baseUrl, tiltakspengerApi.isaliveGetRequestConfig().path),
                tiltakspengerApi.client.client,
            )
        }
}

package no.nav.modiapersonoversikt.consumer.pdlPip

import com.github.benmanes.caffeine.cache.Cache
import no.nav.common.health.HealthCheckUtils
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.types.identer.AktorId
import no.nav.common.types.identer.Fnr
import no.nav.common.utils.UrlUtils
import no.nav.modiapersonoversikt.consumer.pdl.generated.enums.IdentGruppe
import no.nav.modiapersonoversikt.consumer.pdlPipApi.generated.apis.PIPTjenesteForPDLDataKunForSystemerApi
import no.nav.modiapersonoversikt.consumer.pdlPipApi.generated.models.PipAdressebeskyttelse
import no.nav.modiapersonoversikt.consumer.pdlPipApi.generated.models.PipGeografiskTilknytning
import no.nav.modiapersonoversikt.consumer.pdlPipApi.generated.models.PipPersondataResponse
import no.nav.modiapersonoversikt.infrastructure.cache.CacheUtils
import no.nav.modiapersonoversikt.infrastructure.ping.Pingable
import no.nav.personoversikt.common.logging.TjenestekallLogg
import okhttp3.OkHttpClient
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable

interface PdlPipApi : Pingable {
    fun hentFnr(aktorId: AktorId): String?

    fun hentAktorId(fnr: Fnr): String?

    fun hentAdresseBeskyttelse(ident: String): List<PipAdressebeskyttelse>?

    fun hentGeografiskTilknytning(ident: String): PipGeografiskTilknytning?
}

@CacheConfig(cacheNames = ["pdlPipCache"], keyGenerator = "userkeygenerator")
open class PdlPipApiImpl(
    private val url: String,
    private val client: OkHttpClient,
    private val cache: Cache<String, PipPersondataResponse?> = CacheUtils.createCache(),
) : PdlPipApi {
    private val pdlPipApi = PIPTjenesteForPDLDataKunForSystemerApi(url, client)

    @Cacheable
    override fun hentFnr(aktorId: AktorId): String? {
        return hentIdenter(aktorId.get())?.identer?.identer?.find { it.gruppe == IdentGruppe.FOLKEREGISTERIDENT.toString() }?.ident
    }

    @Cacheable
    override fun hentAktorId(fnr: Fnr): String? {
        return hentIdenter(fnr.get())?.identer?.identer?.find { it.gruppe == IdentGruppe.AKTORID.toString() }?.ident
    }

    @Cacheable
    override fun hentAdresseBeskyttelse(ident: String): List<PipAdressebeskyttelse>? {
        return hentIdenter(ident)?.person?.adressebeskyttelse
    }

    @Cacheable
    override fun hentGeografiskTilknytning(ident: String): PipGeografiskTilknytning? {
        return hentIdenter(ident)?.geografiskTilknytning
    }

    private fun hentIdenter(ident: String): PipPersondataResponse? {
        return cache.get(ident) {
            runCatching {
                pdlPipApi.lookupIdent(ident)
            }.getOrElse {
                TjenestekallLogg.error(
                    header = "Greide ikke å hente data fra pdl-pip-api",
                    fields = mapOf("ident" to ident),
                    throwable = it,
                )
                null
            }
        }
    }

    override fun ping() =
        SelfTestCheck(
            "pdl-pip-api via $url",
            false,
        ) {
            HealthCheckUtils.pingUrl(UrlUtils.joinPaths(url, "/internal/health/liveness"), client)
        }
}

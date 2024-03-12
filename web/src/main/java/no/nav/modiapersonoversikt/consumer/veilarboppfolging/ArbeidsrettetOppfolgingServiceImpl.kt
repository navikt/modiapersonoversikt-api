package no.nav.modiapersonoversikt.consumer.veilarboppfolging

import no.nav.common.rest.client.RestClient
import no.nav.common.types.identer.Fnr
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor
import no.nav.modiapersonoversikt.infrastructure.http.OkHttpUtils.objectMapper
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.modiapersonoversikt.utils.BoundedOnBehalfOfTokenClient
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import kotlin.reflect.KClass

@CacheConfig(cacheNames = ["oppfolgingsinfoCache"], keyGenerator = "userkeygenerator")
open class ArbeidsrettetOppfolgingServiceImpl(
    apiUrl: String,
    private val ansattService: AnsattService,
    private val oboTokenProvider: BoundedOnBehalfOfTokenClient,
) : ArbeidsrettetOppfolging.Service {
    private val url = apiUrl.removeSuffix("/")
    private val client =
        RestClient.baseClient().newBuilder()
            .addInterceptor(XCorrelationIdInterceptor())
            .addInterceptor(
                LoggingInterceptor("Oppfolging") {
                    requireNotNull(it.header("X-Correlation-ID")) {
                        "Kall uten \"X-Correlation-ID\" er ikke lov"
                    }
                },
            )
            .addInterceptor(
                AuthorizationInterceptor {
                    AuthContextUtils.requireBoundedClientOboToken(oboTokenProvider)
                },
            )
            .build()

    @Cacheable
    override fun hentOppfolgingsinfo(fodselsnummer: Fnr): ArbeidsrettetOppfolging.Info {
        val oppfolgingstatus = hentOppfolgingStatus(fodselsnummer)
        val enhetOgVeileder =
            when (oppfolgingstatus.underOppfolging) {
                true -> hentOppfolgingsEnhetOgVeileder(fodselsnummer)
                else -> null
            }
        return ArbeidsrettetOppfolging.Info(
            oppfolgingstatus.underOppfolging,
            oppfolgingstatus.erManuell,
            enhetOgVeileder?.veilederId?.let { ansattService.hentVeileder(NavIdent(it)) },
            enhetOgVeileder?.oppfolgingsenhet?.let {
                ArbeidsrettetOppfolging.Enhet(
                    it.enhetId,
                    it.navn,
                )
            },
        )
    }

    @Cacheable
    override fun hentOppfolgingStatus(fodselsnummer: Fnr): ArbeidsrettetOppfolging.Status {
        return client.fetchJson(
            url = "$url/underoppfolging?fnr=${fodselsnummer.get()}",
            type = ArbeidsrettetOppfolging.Status::class,
        )
    }

    override fun ping() {
        val request =
            Request.Builder()
                .url("$url/ping")
                .build()

        RestClient.baseClient()
            .newCall(request)
            .execute()
            .body()
            ?.string()
    }

    private fun hentOppfolgingsEnhetOgVeileder(fodselsnummer: Fnr): ArbeidsrettetOppfolging.EnhetOgVeileder {
        return client.fetchJson(
            url = "$url/person/${fodselsnummer.get()}/oppfolgingsstatus",
            type = ArbeidsrettetOppfolging.EnhetOgVeileder::class,
        )
    }

    private fun <T : Any> OkHttpClient.fetchJson(
        url: String,
        type: KClass<T>,
    ): T {
        val request = Request.Builder().url(url).build()
        val response = this.newCall(request).execute()
        val statusCode = response.code()
        val body = response.body()?.string()

        if (statusCode in 200 until 300) {
            return objectMapper.readValue(body, type.java)
        } else {
            throw IllegalStateException("Forventet 200-range svar og body fra oppfolging-api, men fikk: $statusCode\n$body")
        }
    }
}

package no.nav.modiapersonoversikt.consumer.veilarboppfolging

import no.nav.common.rest.client.RestClient
import no.nav.common.types.identer.Fnr
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.infrastructure.http.OkHttpUtils.objectMapper
import no.nav.modiapersonoversikt.rest.common.FnrRequest
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import kotlin.reflect.KClass

@CacheConfig(cacheNames = ["oppfolgingsinfoCache"], keyGenerator = "userkeygenerator")
open class ArbeidsrettetOppfolgingServiceImpl(
    apiUrl: String,
    private val ansattService: AnsattService,
    private val httpClient: OkHttpClient,
) : ArbeidsrettetOppfolging.Service {
    private val url = apiUrl.removeSuffix("/")

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
    override fun hentOppfolgingStatus(fodselsnummer: Fnr): ArbeidsrettetOppfolging.Status =
        httpClient.fetchJson(
            url = "$url/underoppfolging?fnr=${fodselsnummer.get()}",
            type = ArbeidsrettetOppfolging.Status::class,
        )

    override fun ping() {
        val request =
            Request
                .Builder()
                .url("$url/ping")
                .build()

        RestClient
            .baseClient()
            .newCall(request)
            .execute()
            .body
            ?.string()
    }

    private fun hentOppfolgingsEnhetOgVeileder(fodselsnummer: Fnr): ArbeidsrettetOppfolging.EnhetOgVeileder =
        httpClient.fetchJson(
            url = "$url/v2/person/hent-oppfolgingsstatus",
            requestBody = FnrRequest(fnr = fodselsnummer.get()).toRequestBody(),
            type = ArbeidsrettetOppfolging.EnhetOgVeileder::class,
        )

    private fun <T : Any> OkHttpClient.fetchJson(
        url: String,
        requestBody: RequestBody? = null,
        type: KClass<T>,
    ): T {
        val request =
            Request
                .Builder()
                .url(url)
                .apply {
                    requestBody?.let {
                        this.post(it)
                    }
                }.build()
        val response = this.newCall(request).execute()
        val statusCode = response.code
        val body = response.body?.string()

        if (statusCode in 200 until 300) {
            return objectMapper.readValue(body, type.java)
        } else {
            throw IllegalStateException("Forventet 200-range svar og body fra oppfolging-api, men fikk: $statusCode\n$body")
        }
    }
}

fun FnrRequest.toRequestBody(): RequestBody = objectMapper.writeValueAsString(this).toRequestBody("application/json".toMediaType())

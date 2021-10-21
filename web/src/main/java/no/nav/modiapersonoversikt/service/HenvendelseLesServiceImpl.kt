package no.nav.modiapersonoversikt.service

import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.SubjectHandler
import no.nav.common.json.JsonMapper
import no.nav.common.rest.client.RestClient
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
import no.nav.modiapersonoversikt.infrastructure.http.getCallId
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.apis.HenvendelseInfoApi
import no.nav.modiapersonoversikt.legacy.api.service.HenvendelseLesService
import no.nav.modiapersonoversikt.service.sfhenvendelse.fixKjedeId
import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import okhttp3.Request

class HenvendelseLesServiceImpl(
    private val systemTokenProvider: SystemUserTokenProvider,
    private val sfHenvendelse: HenvendelseInfoApi,
    private val unleash: UnleashService
) : HenvendelseLesService {
    private val objectMapper = JsonMapper.defaultObjectMapper()
    private val baseUrl: String = getRequiredProperty("HENVENDELSE_LES_API_URL")

    override fun alleBehandlingsIderTilhorerBruker(fnr: String, behandlingsIder: List<String>): Boolean {
        return if (unleash.isEnabled(Feature.USE_SALESFORCE_DIALOG)) {
            val kjedeId = behandlingsIder.map { it.fixKjedeId() }.distinct()
            require(kjedeId.size == 1) {
                "Fant flere unike kjedeIder i samme spørring. Dette skal ikke være mulig mot SF"
            }
            val henvendelse = sfHenvendelse.henvendelseinfoHenvendelseKjedeIdGet(kjedeId.first(), getCallId())
            henvendelse.fnr == fnr
        } else {
            val queryparams = byggQueryparams(
                "fnr" to fnr,
                "id" to behandlingsIder
            )
            fetch("$baseUrl/behandlingsider?$queryparams")
        }
    }

    private inline fun <reified T : Any> fetch(url: String): T {
        val token = SubjectHandler.getSsoToken(SsoToken.Type.OIDC).orElseThrow { RuntimeException("Fant ikke OIDC-token") }
        return RestClient.baseClient()
            .newCall(
                Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer $token")
                    .header("SystemAuthorization", "Bearer ${systemTokenProvider.systemUserToken}")
                    .build()
            )
            .execute()
            .body()!!
            .string()
            .let { objectMapper.readValue(it, T::class.java) }
    }

    private fun byggQueryparams(vararg pairs: Pair<String, Any>): String {
        return pairs
            .flatMap { pair ->
                if (pair.second is Iterable<*>) {
                    (pair.second as Iterable<*>).map { Pair(pair.first, it.toString()) }
                } else {
                    listOf(Pair(pair.first, pair.second.toString()))
                }
            }.joinToString("&") { "${it.first}=${it.second}" }
    }
}

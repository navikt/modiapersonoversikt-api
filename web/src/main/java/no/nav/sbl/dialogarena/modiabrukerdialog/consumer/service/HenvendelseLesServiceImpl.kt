package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service

import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.SubjectHandler
import no.nav.common.json.JsonMapper
import no.nav.common.rest.client.RestClient
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.common.utils.EnvironmentUtils.getRequiredProperty
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseLesService
import okhttp3.Request

class HenvendelseLesServiceImpl(private val systemTokenProvider: SystemUserTokenProvider) : HenvendelseLesService {
    private val objectMapper = JsonMapper.defaultObjectMapper()
    private val baseUrl: String = getRequiredProperty("HENVENDELSE_LES_API_URL")

    override fun alleBehandlingsIderTilhorerBruker(fnr: String, behandlingsIder: List<String>): Boolean {
        val queryparams = byggQueryparams(
            "fnr" to fnr,
            "id" to behandlingsIder
        )
        return fetch("$baseUrl/behandlingsider?$queryparams")
    }

    override fun alleHenvendelseIderTilhorerBruker(fnr: String, henvendelseIder: List<String>): Boolean {
        val queryparams = byggQueryparams(
            "fnr" to fnr,
            "id" to henvendelseIder
        )
        return fetch("$baseUrl/henvendelseider?$queryparams")
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

package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service

import no.nav.brukerdialog.security.oidc.SystemUserTokenProvider
import no.nav.common.auth.SsoToken
import no.nav.common.auth.SubjectHandler
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseLesService
import no.nav.sbl.rest.RestUtils
import no.nav.sbl.util.EnvironmentUtils

class HenvendelseLesServiceImpl : HenvendelseLesService {
    private val baseUrl: String = EnvironmentUtils.getRequiredProperty("henvendelse-les.api.url")
    private val systemTokenProvider = SystemUserTokenProvider()

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

    private inline fun <reified T> fetch(url: String): T {
        val token = SubjectHandler.getSsoToken(SsoToken.Type.OIDC).orElseThrow { RuntimeException("Fant ikke OIDC-token") }
        return RestUtils.withClient {
            it
                    .target(url)
                    .request()
                    .header("Authorization", "Bearer $token")
                    .header("SystemAuthorization", "Bearer ${systemTokenProvider.token}")
                    .get(T::class.java)
        }
    }

    private fun byggQueryparams(vararg pairs: Pair<String, Any>): String {
        return pairs
                .flatMap { pair ->
                    if (pair.second is Iterable<*>) {
                        (pair.second as Iterable<*>).map { Pair(pair.first, it.toString()) }
                    } else {
                        listOf(Pair(pair.first, pair.second.toString()))
                    }
                }
                .map { "${it.first}=${it.second}" }
                .joinToString("&")
    }
}
package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service

import no.nav.brukerdialog.security.context.SubjectHandler
import no.nav.brukerdialog.security.oidc.SystemUserTokenProvider
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseLesService
import no.nav.sbl.rest.RestUtils

class HenvendelseLesServiceImpl : HenvendelseLesService {
    private val baseUrl: String = System.getProperty("henvendelse-les.api.url")
    private val systemTokenProvider = SystemUserTokenProvider()

    override fun alleBehandlingsIderTilhorerBruker(fnr: String, behandlingsIder: List<String>): Boolean {
        val queryparams = byggQueryparams(
                "fnr" to fnr,
                "id" to behandlingsIder
        )
        return fetch("$baseUrl/behandlingsider?$queryparams")
    }

    private inline fun <reified T> fetch(url: String): T {
        return RestUtils.withClient {
            it
                    .target(url)
                    .request()
                    .header("Authorization", "Bearer ${SubjectHandler.getSubjectHandler().internSsoToken}")
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